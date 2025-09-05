package org.yarhooshmand.smartv3.keys

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import org.json.JSONArray
import java.util.concurrent.atomic.AtomicInteger

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object KeysManager {
    private const val PREFS = "yar_keys_prefs"
    private const val KEY_LIST = "api_keys"
    private const val CUR_INDEX = "cur_index"
    private const val BAD_KEYS = "bad_keys" // stores keys with cooldown timestamp
    // The direct Google Drive "uc?export=download&id=..." link (user-provided)
    private const val DRIVE_KEYS_URL = "https://drive.google.com/uc?export=download&id=17iwkjyGcxJeDgwQWEcsOdfbOxOah_0u0"

    private val client = OkHttpClient.Builder().callTimeout(20, TimeUnit.SECONDS).build()
    @Volatile private var cached: MutableList<String> = mutableListOf()
    private val idx = AtomicInteger(0)
    // cooldown for bad keys in ms (e.g., 10 minutes)
    private const val BAD_KEY_COOLDOWN_MS = 10 * 60 * 1000L

    suspend fun init(ctx: Context) = withContext(Dispatchers.IO) {
        val sp = try {
            android.security.keystore.KeyProperties::class // guard import
            androidx.security.crypto.EncryptedSharedPreferences.create(
                PREFS,
                androidx.security.crypto.MasterKeys.getOrCreate(androidx.security.crypto.MasterKeys.AES256_GCM_SPEC),
                ctx,
                androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        }
        // load persisted keys first
        val saved = sp.getString(KEY_LIST, null)
        val local = saved?.split('|')?.map { it.trim() }?.filter { it.isNotEmpty() }?.toMutableList() ?: mutableListOf()
        cached = local

        // try fetching remote keys from Google Drive; merge without duplicates
        try {
            var remote = fetchFromDriveWithPassword(org.yarhooshmand.smartv3.keys.PassphraseManager.get(ctx))
            if (remote.isEmpty()) { remote = fetchFromDriveWithPassword("12345") }
            if (remote.isNotEmpty()) {
                // Merge: remote first, then local extras (preserve order, unique)
                val merged = LinkedHashSet<String>()
                remote.forEach { merged.add(it) }
                local.forEach { merged.add(it) }
                cached = merged.toMutableList()
                // persist merged set
                sp.edit().putString(KEY_LIST, cached.joinToString("|")).apply()
            }
        } catch (_: Exception) { /* ignore */ }

        // restore index
        idx.set(sp.getInt(CUR_INDEX, 0).coerceAtLeast(0))
        // cleanup any expired bad keys
        cleanupBadKeys(sp)
    }

    fun setKeys(ctx: Context, keys: List<String>) {
        cached = keys.filter { it.isNotBlank() }.toMutableList()
        val sp = try {
            android.security.keystore.KeyProperties::class // guard import
            androidx.security.crypto.EncryptedSharedPreferences.create(
                PREFS,
                androidx.security.crypto.MasterKeys.getOrCreate(androidx.security.crypto.MasterKeys.AES256_GCM_SPEC),
                ctx,
                androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        }
        sp.edit().putString(KEY_LIST, cached.joinToString("|")).apply()
        if (idx.get() >= cached.size) idx.set(0)
        sp.edit().putInt(CUR_INDEX, idx.get()).apply()
    }

    fun getActiveKey(ctx: Context): String? {
        if (cached.isEmpty()) return null
        val sp = try {
            android.security.keystore.KeyProperties::class // guard import
            androidx.security.crypto.EncryptedSharedPreferences.create(
                PREFS,
                androidx.security.crypto.MasterKeys.getOrCreate(androidx.security.crypto.MasterKeys.AES256_GCM_SPEC),
                ctx,
                androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        }
        cleanupBadKeys(sp)
        val size = cached.size
        if (size == 0) return null
        // try up to `size` times to find a key not marked bad
        for (attempt in 0 until size) {
            val i = idx.getAndUpdate { (it + 1) % size }
            val key = cached[i]
            if (!isKeyMarkedBad(sp, key)) return key
        }
        // if all keys are bad, return the current one (will likely fail)
        return cached[idx.get() % size]
    }

    private fun isKeyMarkedBad(sp: android.content.SharedPreferences, key: String): Boolean {
        val raw = sp.getString(BAD_KEYS, null) ?: return false
        try {
            val obj = org.json.JSONObject(raw)
            if (!obj.has(key)) return false
            val until = obj.getLong(key)
            return System.currentTimeMillis() < until
        } catch (_: Exception) { return false }
    }

    private fun markKeyBad(sp: android.content.SharedPreferences, key: String) {
        try {
            val raw = sp.getString(BAD_KEYS, null)
            val obj = if (raw.isNullOrBlank()) org.json.JSONObject() else org.json.JSONObject(raw)
            obj.put(key, System.currentTimeMillis() + BAD_KEY_COOLDOWN_MS)
            sp.edit().putString(BAD_KEYS, obj.toString()).apply()
        } catch (_: Exception) { /* ignore */ }
    }

    private fun cleanupBadKeys(sp: android.content.SharedPreferences) {
        try {
            val raw = sp.getString(BAD_KEYS, null) ?: return
            val obj = org.json.JSONObject(raw)
            val it = obj.keys()
            val remove = mutableListOf<String>()
            while (it.hasNext()) {
                val k = it.next()
                val until = obj.getLong(k)
                if (System.currentTimeMillis() >= until) remove.add(k)
            }
            remove.forEach { obj.remove(it) }
            sp.edit().putString(BAD_KEYS, obj.toString()).apply()
        } catch (_: Exception) { /* ignore */ }
    }

    
    private fun decryptBase64AesGcmPBKDF2(encryptedBase64: String, password: String): String? {
        try {
            val cleaned = encryptedBase64.trim()
            val data = Base64.decode(cleaned, Base64.DEFAULT)
            if (data.size < 16 + 12 + 1) return null
            val salt = data.copyOfRange(0, 16)
            val iv = data.copyOfRange(16, 28)
            val ct = data.copyOfRange(28, data.size)
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(password.toCharArray(), salt, 20000, 256)
            val tmp = factory.generateSecret(spec).encoded
            val keySpec = SecretKeySpec(tmp, "AES")
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val gcm = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcm)
            val plain = cipher.doFinal(ct)
            return String(plain, Charsets.UTF_8)
        } catch (e: Exception) {
            // decryption failed
            return null
        }
    }


    private fun fetchFromDrive(): List<String> {
        // passphrase read from secure prefs if available; otherwise we will also try fallback later in init
        return fetchFromDriveWithPassword(null)
    }.filter { it.isNotEmpty() }
                }
            }
            // fallback: assume plain text keys, one per line
            body.split('\n').map { it.trim() }.filter { it.isNotEmpty() }
        } catch (_: Exception) {
            emptyList()
        }
    }
 catch (_: Exception) {
            emptyList()
        }
    }

    // Validate a key by calling the minimal GET /v1/models endpoint.
    // Returns true if key appears valid (HTTP 200), false otherwise.
    fun validateKey(key: String): Boolean {
        return try {
            val req = Request.Builder()
                .url("https://api.openai.com/v1/models")
                .addHeader("Authorization", "Bearer $key")
                .get()
                .build()
            client.newCall(req).execute().use { resp ->
                resp.isSuccessful
            }
        } catch (_: Exception) { false }
    }

    // Try to get a working key; if a key fails during use, call reportBadKey to mark it.
    fun reportBadKey(ctx: Context, key: String) {
        val sp = try {
            android.security.keystore.KeyProperties::class // guard import
            androidx.security.crypto.EncryptedSharedPreferences.create(
                PREFS,
                androidx.security.crypto.MasterKeys.getOrCreate(androidx.security.crypto.MasterKeys.AES256_GCM_SPEC),
                ctx,
                androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        }
        markKeyBad(sp, key)
    }

    // Internal helper used by network code that doesn't have Context readily available
    fun reportBadKeyInternal(key: String) {
        // mark the key in-memory; persisted marking requires Context
        try {
            if (cached.isEmpty()) return
            // advance index to skip this key next time
            idx.getAndUpdate { (it + 1) % cached.size }
        } catch (_: Exception) {}
    }

    // Return an active key string without context; null if none
    fun getActiveKeySafe(): String? {
        return if (cached.isEmpty()) null else cached[idx.get() % cached.size]
    }


}
