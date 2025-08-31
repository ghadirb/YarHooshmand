
package org.yarhooshmand.smartv3.models

import android.content.Context
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.yarhooshmand.smartv3.keys.KeysManager
import java.util.concurrent.TimeUnit

data class ModelStatus(val name: String, var enabled: Boolean, var online: Boolean = false)

object ModelManager {
    private const val PREFS = "yar_models_prefs"
    private const val MODELS = "models"
    private const val SIMPLE_MODE = "simple_mode"
    private const val LISTEN_AFTER_ALARM = "listen_after_alarm"
    private val gson = Gson()
    private val client = OkHttpClient.Builder().callTimeout(20, TimeUnit.SECONDS).build()
    private val json = "application/json; charset=utf-8".toMediaType()
    private var cache: MutableList<ModelStatus> = mutableListOf()

    fun init(ctx: Context) {
        if (cache.isEmpty()) {
            val saved = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(MODELS, null)
            if (saved == null) {
                cache = mutableListOf(
                    ModelStatus("gpt-4o-mini", true),
                    ModelStatus("gpt-4o", true),
                    ModelStatus("gpt-3.5-turbo", true)
                )
                persist(ctx)
            } else {
                cache = gson.fromJson(saved, Array<ModelStatus>::class.java).toMutableList()
            }
        }
    }

    fun getModels(ctx: Context): MutableList<ModelStatus> { init(ctx); return cache }

    fun updateModelEnabled(ctx: Context, name: String, enabled: Boolean) {
        init(ctx); cache.find { it.name == name }?.enabled = enabled; persist(ctx)
    }

    fun setSimpleMode(ctx: Context, simple: Boolean) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(SIMPLE_MODE, simple).apply()
    }
    fun isSimpleMode(ctx: Context): Boolean =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(SIMPLE_MODE, false)

    fun setListenAfterAlarm(ctx: Context, enabled: Boolean) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(LISTEN_AFTER_ALARM, enabled).apply()
    }
    fun isListenAfterAlarm(ctx: Context): Boolean =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(LISTEN_AFTER_ALARM, true)

    private fun persist(ctx: Context) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(MODELS, gson.toJson(cache)).apply()
    }

    fun pickBestActive(): String? {
        val active = cache.filter { it.enabled }
        return active.firstOrNull { it.name.contains("4o-mini") }?.name
            ?: active.firstOrNull { it.name == "gpt-4o" }?.name
            ?: active.firstOrNull()?.name
    }

    fun refreshStatuses(ctx: Context): List<ModelStatus> {
        val keys = KeysManager.getKeys(ctx)
        if (keys.isEmpty()) return cache.map { it.copy(online = false) }
        val key = keys.first()
        for (m in cache) {
            if (!m.enabled) { m.online = false; continue }
            m.online = testModel(key, m.name)
        }
        persist(ctx); return cache
    }

    private fun testModel(key: String, model: String): Boolean {
        return try {
            val url = "https://api.openai.com/v1/chat/completions"
            val body = gson.toJson(mapOf(
                "model" to model,
                "messages" to listOf(mapOf("role" to "user", "content" to "ping")),
                "max_tokens" to 1
            ))
            val req = Request.Builder().url(url)
                .addHeader("Authorization", "Bearer $key")
                .addHeader("Content-Type", "application/json")
                .post(body.toRequestBody(json)).build()
            val resp = client.newCall(req).execute()
            resp.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
