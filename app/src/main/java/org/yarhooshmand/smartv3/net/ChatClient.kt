package org.yarhooshmand.smartv3.net

import android.content.Context
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

    fun chatLocal(ctx: Context, prompt: String): String? {
        val key = KeysManager.getActiveKey(ctx)
        if (key.isNullOrBlank()) {
            return when {
                prompt.contains("سلام") -> "سلام! چطور می‌تونم کمک کنم؟"
                prompt.trim().endsWith("?") -> "سؤال خوبیه! فعلاً به اینترنت متصل نیستم، اما می‌تونم پاسخ ساده بدم."
                else -> "پیامت رسید. برای پاسخ‌های بهتر، از تنظیمات کلید API وارد کن."
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

        val tried = mutableSetOf<String>()
        var lastEx: Exception? = null

        while (true) {
            val curKey = KeysManager.getActiveKeySafe() ?: break
            if (!tried.add(curKey)) break

            try {
                val req = Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer $curKey")
                    .addHeader("Content-Type", "application/json")
                    .post(body.toRequestBody(json))
                    .build()

                val resp = client.newCall(req).execute()
                var tryNext = false
                resp.use { r ->
                    val text = r.body?.string()
                    if (!r.isSuccessful || text == null) {
                        if (r.code == 401 || r.code == 403) {
                            KeysManager.reportBadKeyInternal(curKey)
                            tryNext = true
                        } else {
                            return null
                        }
                    } else {
                        val obj = gson.fromJson(text, Map::class.java)
                        val choices = obj["choices"] as? List<*>
                        val first = choices?.firstOrNull() as? Map<*, *>
                        val msg = first?.get("message") as? Map<*, *>
                        val content = msg?.get("content") as? String
                        return content?.trim()
                    }
                }
                if (tryNext) continue
            } catch (ex: Exception) {
                lastEx = ex
                KeysManager.reportBadKeyInternal(curKey)
                // می‌ریم سراغ کلید بعدی
            }
        }
        return null
    }
}
