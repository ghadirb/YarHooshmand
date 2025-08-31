package org.yarhooshmand.smartv3.keys

import android.content.Context
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object KeysManager {
    private const val PREFS = "yar_keys_prefs"
    private const val KEY_LIST = "api_keys"
    private const val LAST_FETCH = "last_fetch_time"
    private val client = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
    private var cached: List<String> = emptyList()

    suspend fun init(ctx: Context) {
        // Load from cache first
        cached = loadKeys(ctx)
        
        // Check if we need to refresh (every 24 hours)
        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val lastFetch = prefs.getLong(LAST_FETCH, 0)
        val now = System.currentTimeMillis()
        
        if (cached.isEmpty() || (now - lastFetch) > 24 * 60 * 60 * 1000) {
            val downloaded = fetchFromDrive()
            if (downloaded.isNotEmpty()) {
                saveKeys(ctx, downloaded)
                prefs.edit().putLong(LAST_FETCH, now).apply()
                cached = downloaded
            }
        }
    }

    fun getKeys(ctx: Context): List<String> {
        if (cached.isEmpty()) cached = loadKeys(ctx)
        return cached
    }

    fun saveKeys(ctx: Context, keys: List<String>) {
        val validKeys = keys.filter { it.startsWith("sk-") && it.length > 20 }
        val joined = validKeys.joinToString("\n")
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LIST, joined)
            .apply()
        cached = validKeys
    }

    private fun loadKeys(ctx: Context): List<String> {
        val raw = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LIST, "") ?: ""
        return raw.split('\n')
            .map { it.trim() }
            .filter { it.isNotEmpty() && it.startsWith("sk-") }
    }

    private suspend fun fetchFromDrive(): List<String> {
        repeat(3) { attempt ->
            try {
                val req = Request.Builder()
                    .url("https://drive.google.com/uc?export=download&id=17iwkjyGcxJeDgwQWEcsOdfbOxOah_0u0")
                    .addHeader("User-Agent", "YarHooshmand/3.0")
                    .build()
                    
                val resp = client.newCall(req).execute()
                if (resp.isSuccessful) {
                    val body = resp.body?.string() ?: ""
                    val keys = body.split('\n')
                        .map { it.trim() }
                        .filter { it.isNotEmpty() && it.startsWith("sk-") }
                    
                    if (keys.isNotEmpty()) {
                        return keys
                    }
                }
            } catch (e: Exception) {
                if (attempt == 2) break // Last attempt
                delay(1000L * (attempt + 1)) // Progressive delay
            }
        }
        return emptyList()
    }

    fun forceRefresh(ctx: Context, callback: (Boolean) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val keys = fetchFromDrive()
            val success = keys.isNotEmpty()
            if (success) {
                saveKeys(ctx, keys)
                ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .putLong(LAST_FETCH, System.currentTimeMillis())
                    .apply()
            }
            launch(Dispatchers.Main) { callback(success) }
        }
    }
}