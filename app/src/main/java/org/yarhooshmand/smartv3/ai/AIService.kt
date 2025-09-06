package org.yarhooshmand.smartv3.ai

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.yarhooshmand.smartv3.keys.KeysManager
import org.yarhooshmand.smartv3.YarApp

object AIService {

    private val http: OkHttpClient by lazy { OkHttpClient.Builder().build() }

    private fun jsonBody(json: String): RequestBody =
        json.toRequestBody("application/json".toMediaType())

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

        val payload = """
        {
          "model": "$model",
          "messages": [{"role": "user", "content": ${jsonEscape(prompt)}}],
          "temperature": 0.2
        }
        """.trimIndent()

        val req = if (provider.lowercase() == "openrouter")
            buildOpenRouterRequest(key, payload) else buildOpenAIRequest(key, payload)

        http.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return "HTTP_${'$'}{resp.code}"
            val body = resp.body?.string().orEmpty()
            return parseTextFromResponse(body)
        }
    }

    // Very small JSON extractor for OpenAI/OpenRouter style payloads
    private fun parseTextFromResponse(body: String): String {
        val key = ""content""
        val idx = body.indexOf(key)
        if (idx < 0) return body
        val start = body.indexOf(':', idx) + 1
        if (start <= 0) return body
        val q1 = body.indexOf('"', start)
        if (q1 < 0) return body
        var i = q1 + 1
        val sb = StringBuilder()
        var escape = false
        while (i < body.length) {
            val c = body[i]
            if (escape) {
                sb.append(c)
                escape = False
            } else {
                if (c == '\\') {
                    escape = true
                } else if (c == '"') {
                    break
                } else {
                    sb.append(c)
                }
            }
            i++
        }
        return sb.toString()
    }

    private fun jsonEscape(s: String): String = """ + s
        .replace("\\", "\\\\")
        .replace(""", "\\"")
        .replace("\n", "\\n")
        + """
}
