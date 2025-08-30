
package org.yarhooshmand.smartv3.net

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.yarhooshmand.smartv3.keys.KeysManager
import org.yarhooshmand.smartv3.models.ModelManager
import java.util.concurrent.TimeUnit

object ChatClient {
    private val client = OkHttpClient.Builder().callTimeout(60, TimeUnit.SECONDS).build()
    private val gson = Gson()
    private val json = "application/json; charset=utf-8".toMediaType()

    fun chatLocal(prompt: String): String? {
        return try { chatNetwork(prompt) ?: "عدم دریافت پاسخ از مدل‌ها" }
        catch (_: Exception) { "پاسخ آزمایشی (آفلاین): $prompt" }
    }

    private fun chatNetwork(prompt: String): String? { return null }

    fun chat(ctx: android.content.Context, prompt: String): String {
        val keys = KeysManager.getKeys(ctx)
        if (keys.isEmpty()) return "هیچ کلید API در دسترس نیست"
        val models = ModelManager.getModels(ctx).filter { it.enabled }.map { it.name }
        for (key in keys) {
            for (m in models) {
                val resp = tryChat(key, m, prompt)
                if (resp != null) return resp
            }
        }
        return "عدم موفقیت در دریافت پاسخ از مدل‌ها"
    }

    private fun tryChat(key: String, model: String, prompt: String): String? {
        return try {
            val url = "https://api.openai.com/v1/chat/completions"
            val body = gson.toJson(mapOf(
                "model" to model,
                "messages" to listOf(mapOf("role" to "user", "content" to prompt)),
                "max_tokens" to 256,
                "temperature" to 0.7
            ))
            val req = Request.Builder().url(url)
                .addHeader("Authorization","Bearer $key")
                .addHeader("Content-Type","application/json")
                .post(body.toRequestBody(json)).build()
            val resp = client.newCall(req).execute()
            val text = resp.body?.string()
            if (!resp.isSuccessful || text == null) return null
            val obj = gson.fromJson(text, Map::class.java)
            val choices = obj["choices"] as? List<*>
            val first = choices?.firstOrNull() as? Map<*, *>
            val msg = first?.get("message") as? Map<*, *>
            val content = msg?.get("content") as? String
            content?.trim()
        } catch (_: Exception) { null }
    }
}
