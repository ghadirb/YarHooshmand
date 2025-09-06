package org.yarhooshmand.smartv3.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.yarhooshmand.smartv3.keys.KeysManager
import org.yarhooshmand.smartv3.YarApp
import java.io.IOException

object AIService {
    private val httpClient: OkHttpClient by lazy { OkHttpClient.Builder().build() }

    /**
     * Call OpenAI or OpenRouter chat completions synchronously inside IO dispatcher.
     *
     * @param prompt text prompt to send to model
     * @param provider either "openai" (default) or "openrouter"
     * @param model model id (e.g. "gpt-3.5-turbo" or other)
     * @return short textual result or an error indicator string
     */
    suspend fun chatOnce(
        prompt: String,
        provider: String = "openai",
        model: String = "gpt-3.5-turbo"
    ): String = withContext(Dispatchers.IO) {
        val ctx = YarApp.context()
        val apiKey = KeysManager.getActiveKeySafe(ctx) ?: return@withContext "NO_KEY_CONFIGURED"

        // Build minimal valid OpenAI-style payload for chat completions
        val payload = buildString {
            append("{")
            append("\"model\":\"").append(escapeJson(model)).append("\",")
            append("\"messages\":[{\"role\":\"user\",\"content\":")
            append(escapeJsonQuoted(prompt)).append("}],")
            append("\"temperature\":0.2")
            append("}")
        }

        val request = try {
            if (provider.equals("openrouter", ignoreCase = true)) {
                buildOpenRouterRequest(apiKey, payload)
            } else {
                buildOpenAIRequest(apiKey, payload)
            }
        } catch (e: Exception) {
            return@withContext "REQUEST_BUILD_ERROR: ${e.message}"
        }

        try {
            httpClient.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) {
                    return@withContext "HTTP_${resp.code}"
                }
                val body = resp.body?.string().orEmpty()
                // try to extract the assistant content — best-effort naive extraction
                return@withContext parseTextFromResponse(body)
            }
        } catch (e: IOException) {
            return@withContext "NETWORK_ERROR: ${e.message}"
        } catch (e: Exception) {
            return@withContext "ERROR: ${e.message}"
        }
    }

    private fun buildOpenAIRequest(apiKey: String, payload: String): Request =
        Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(payload.toRequestBody("application/json".toMediaType()))
            .build()

    private fun buildOpenRouterRequest(apiKey: String, payload: String): Request =
        Request.Builder()
            .url("https://openrouter.ai/api/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(payload.toRequestBody("application/json".toMediaType()))
            .build()

    /**
     * Naive extraction of text content from OpenAI-like JSON responses.
     * This is intentionally permissive — for production use parse JSON properly
     * (e.g. kotlinx.serialization or Moshi/Gson) and navigate choices[].message.content.
     */
    private fun parseTextFromResponse(body: String): String {
        // Look for the first occurrence of "content": "...."
        val marker = "\"content\""
        var idx = body.indexOf(marker)
        if (idx < 0) return body.take(4000) // fallback: return raw body (trimmed)
        idx = body.indexOf(':', idx)
        if (idx < 0) return body.take(4000)
        // find first quote after colon
        val firstQuote = body.indexOf('\"', idx)
        if (firstQuote < 0) return body.take(4000)
        val sb = StringBuilder()
        var escaped = false
        var i = firstQuote + 1
        while (i < body.length) {
            val c = body[i]
            if (escaped) {
                // append as-is (no complex unescape)
                sb.append(c)
                escaped = false
            } else {
                when (c) {
                    '\\' -> escaped = true
                    '\"' -> break
                    else -> sb.append(c)
                }
            }
            i++
        }
        val result = sb.toString()
        return if (result.isNotEmpty()) result else body.take(4000)
    }

    private fun escapeJson(s: String): String =
        s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")

    // produce a quoted JSON string, i.e. returns "..." including surrounding quotes
    private fun escapeJsonQuoted(s: String): String = "\"" + escapeJson(s) + "\""
}
