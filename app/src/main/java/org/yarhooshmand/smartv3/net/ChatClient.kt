package org.yarhooshmand.smartv3.net

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.yarhooshmand.smartv3.keys.KeysManager
import org.yarhooshmand.smartv3.models.ModelManager
import android.content.Context
import java.util.concurrent.TimeUnit

object ChatClient {
    private val client = OkHttpClient.Builder().callTimeout(60, TimeUnit.SECONDS).build()
    private val gson = Gson()
    private val json = "application/json; charset=utf-8".toMediaType()

    fun chatLocal(ctx: Context, prompt: String): String? {
        // If no API key configured, return a friendly local reply.
        val key = KeysManager.getActiveKey(ctx)
        if (key.isNullOrBlank()) {
            return when {
                prompt.contains("سلام") -> "سلام! چطور می‌تونم کمک کنم؟"
                prompt.trim().endsWith("?") -> "سوال خوبیه! فعلاً به اینترنت متصل نیستم، ولی می‌تونم به شکل محلی پاسخ ساده بدم."
                else -> "پیامت رسید. برای پاسخ‌های بهتر، از بخش تنظیمات کلید API رو وارد کن."
            }
        }
        val model = ModelManager.firstEnabled() ?: "gpt-4o-mini"
        val body = gson.toJson(
            mapOf(
                "model" to model,
                "messages" to listOf(
                    mapOf("role" to "system", "content" to "You are a helpful assistant speaking Persian."),
                    mapOf("role" to "user", "content" to prompt)
                )
            )
        )
        // try keys until success or exhausted
        val tried = mutableSetOf<String>()
        var lastEx: Exception? = null
        while (true) {
            val curKey = KeysManager.getActiveKeySafe() ?: break
            if (tried.contains(curKey)) break
            tried.add(curKey)
            try {
                val req = Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer $curKey")
                    .addHeader("Content-Type", "application/json")
                    .post(body.toRequestBody(json))
                    .build()
                client.newCall(req).execute().use { resp ->
                    val text = resp.body?.string()
                    if (!resp.isSuccessful || text == null) {
                        // if auth error, mark key bad and try next
                        if (resp.code == 401 || resp.code == 403) {
                            KeysManager.reportBadKeyInternal(curKey)
                            continue
                        }
                        return null
                    }
                    val obj = gson.fromJson(text, Map::class.java)
                    val choices = obj["choices"] as? List<*>
                    val first = choices?.firstOrNull() as? Map<*, *>
                    val msg = first?.get("message") as? Map<*, *>
                    val content = msg?.get("content") as? String
                    return content?.trim()
                }
            } catch (ex: Exception) {
                lastEx = ex
                // if network-level exception, mark that key bad briefly and try next
                KeysManager.reportBadKeyInternal(curKey)
                continue
            }
        }
        return null
    }
}
