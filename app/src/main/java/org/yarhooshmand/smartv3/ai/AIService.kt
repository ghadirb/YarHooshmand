package org.yarhooshmand.smartv3.ai

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.yarhooshmand.smartv3.keys.KeysManager
import org.yarhooshmand.smartv3.YarApp

object AIService {
    private val http by lazy { OkHttpClient.Builder().build() }

    private fun jsonBody(json: String) = json.toRequestBody("application/json".toMediaTypeOrNull())

    private fun buildOpenAIRequest(apiKey: String, payload: String): Request =
        Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(jsonBody(payload))
            .build()

    private fun buildOpenRouterRequest(apiKey: String, payload: String): Request =
        Request.Builder()
            .url("https://openrouter.ai/api/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(jsonBody(payload))
            .build()

    fun chatOnce(prompt: String, provider: String = "openai", model: String = "gpt-3.5-turbo"): String {
        val ctx = YarApp.context()
        val key = KeysManager.getActiveKeySafe(ctx) ?: return "NO_KEY_CONFIGURED"

        val payload = """{"model":"$model","messages":[{"role":"user","content":${jsonEscape(prompt)}}],"temperature":0.2}"""

        val req = if (provider.equals("openrouter", ignoreCase = true))
            buildOpenRouterRequest(key, payload) else buildOpenAIRequest(key, payload)

        http.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return "HTTP_${'$'}{resp.code}"
            val body = resp.body?.string().orEmpty()
            return parseTextFromResponse(body)
        }
    }

    private fun parseTextFromResponse(body: String): String {
        val marker = ""content""
        val idx = body.indexOf(marker)
        if (idx < 0) return body
        val start = body.indexOf(':', idx)
        if (start < 0) return body
        val q = body.indexOf('"', start)
        if (q < 0) return body
        val sb = StringBuilder()
        var i = q + 1
        var escape = false
        while (i < body.length) {
            val c = body[i]
            if (escape) {
                sb.append(c); escape = false
            } else {
                if (c == '\\') { escape = true }
                else if (c == '"') { break }
                else { sb.append(c) }
            }
            i++
        }
        return sb.toString()
    }

    private fun jsonEscape(s: String): String = ""${s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n")}""
}
