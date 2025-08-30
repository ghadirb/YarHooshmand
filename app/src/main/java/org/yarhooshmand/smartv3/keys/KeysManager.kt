
package org.yarhooshmand.smartv3.keys

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object KeysManager {
    private const val PREFS = "yar_keys_prefs"
    private const val KEY_LIST = "api_keys"
    private val client = OkHttpClient.Builder().callTimeout(20, TimeUnit.SECONDS).build()
    private var cached: List<String> = emptyList()

    suspend fun init(ctx: Context) {
        val dl = fetchFromDrive()
        if (dl.isNotEmpty()) saveKeys(ctx, dl)
        cached = loadKeys(ctx)
    }

    fun getKeys(ctx: Context): List<String> {
        if (cached.isEmpty()) cached = loadKeys(ctx)
        return cached
    }

    fun saveKeys(ctx: Context, keys: List<String>) {
        val joined = keys.joinToString("\n")
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_LIST, joined).apply()
        cached = keys
    }

    private fun loadKeys(ctx: Context): List<String> {
        val raw = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_LIST, "") ?: ""
        return raw.split('\n').map { it.trim() }.filter { it.isNotEmpty() }
    }

    private fun fetchFromDrive(): List<String> {
        return try {
            val req = Request.Builder()
                .url("https://drive.google.com/uc?export=download&id=17iwkjyGcxJeDgwQWEcsOdfbOxOah_0u0")
                .build()
            val resp = client.newCall(req).execute()
            val body = resp.body?.string() ?: ""
            body.split('\n').map { it.trim() }.filter { it.isNotEmpty() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
