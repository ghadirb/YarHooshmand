package org.yarhooshmand.smartv3.ai

import android.content.Context
import okhttp3.*
import org.json.JSONObject
import org.json.JSONArray
import org.yarhooshmand.smartv3.utils.AISettingsPref
import java.util.concurrent.TimeUnit

class AIService(private val ctx: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    fun chat(query: String): Result<String> {
        val primary = AISettingsPref.getDefaultProvider(ctx)
        val fallback = if (AISettingsPref.isFallbackEnabled(ctx)) {
            if (primary == ProviderType.OPENAI) ProviderType.OPENROUTER else ProviderType.OPENAI
        } else null

        val r1 = callProvider(primary, query)
        if (r1.isSuccess) return r1
        if (fallback != null) {
            val r2 = callProvider(fallback, query)
            if (r2.isSuccess) return r2
        }
        return r1 // return first error
    }

    private fun callProvider(p: ProviderType, query: String): Result<String> {
        return try {
            val key = AISettingsPref.getApiKey(ctx, p) ?: return Result.failure(Exception("API key for $p missing"))
            val model = AISettingsPref.getModel(ctx, p) ?: ModelAllowlist.defaultsByProvider[p]!!.id
            val (url, body, headers) = when (p) {
                ProviderType.OPENAI -> buildOpenAI(model, key, query)
                ProviderType.OPENROUTER -> buildOpenRouter(model, key, query)
                ProviderType.ANTHROPIC -> buildAnthropic(model, key, query)
            }
            val reqBuilder = Request.Builder().url(url).post(body)
            headers.forEach { (k, v) -> reqBuilder.addHeader(k, v) }
            val res = client.newCall(reqBuilder.build()).execute()
            if (!res.isSuccessful) {
                return Result.failure(Exception("HTTP ${res.code}: ${res.body?.string()}"))
            }
            val txt = res.body?.string() ?: ""
            val content = parseTextFromResponse(txt)
            Result.success(content)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    private fun buildOpenAI(model: String, key: String, query: String): Triple<String, RequestBody, Map<String,String>> {
        val url = "https://api.openai.com/v1/chat/completions"
        val json = JSONObject()
            .put("model", model)
            .put("messages", listOf(
                JSONObject().put("role", "user").put("content", query)
            ))
        val body = RequestBody.create(MediaType.parse("application/json"), json.toString())
        val headers = mapOf("Authorization" to "Bearer $key")
        return Triple(url, body, headers)
    }

    private fun buildOpenRouter(model: String, key: String, query: String): Triple<String, RequestBody, Map<String,String>> {
        val url = "https://openrouter.ai/api/v1/chat/completions"
        val json = JSONObject()
            .put("model", model)
            .put("messages", listOf(
                JSONObject().put("role", "user").put("content", query)
            ))
        val body = RequestBody.create(MediaType.parse("application/json"), json.toString())
        val headers = mapOf(
            "Authorization" to "Bearer $key",
            "Content-Type" to "application/json"
        )
        return Triple(url, body, headers)
    }

    private fun parseTextFromResponse(jsonStr: String): String {
        val obj = JSONObject(jsonStr)
        val choices = obj.optJSONArray("choices") ?: return jsonStr
        if (choices.length() == 0) return jsonStr
        val first = choices.getJSONObject(0)
        val msg = first.optJSONObject("message")
        val content = msg?.optString("content")
        return content ?: jsonStr
    }
}

    private fun buildAnthropic(model: String, key: String, query: String): Triple<String, RequestBody, Map<String,String>> {
        val url = "https://api.anthropic.com/v1/messages"
        val json = JSONObject()
            .put("model", model)
            .put("max_tokens", 256)
            .put("messages", JSONArray().put(JSONObject().put("role", "user").put("content", query)))
        val body = RequestBody.create(MediaType.parse("application/json"), json.toString())
        val headers = mapOf(
            "x-api-key" to key,
            "anthropic-version" to "2023-06-01",
            "content-type" to "application/json"
        )
        return Triple(url, body, headers)
    }
    

    fun testModel(provider: ProviderType, modelId: String): Result<String> {
        return try {
            val key = org.yarhooshmand.smartv3.utils.AISettingsPref.getApiKey(ctx, provider) ?: return Result.failure(Exception("API key missing"))
            val (url, body, headers) = when (provider) {
                ProviderType.OPENAI -> buildOpenAI(modelId, key, "سلام")
                ProviderType.OPENROUTER -> buildOpenRouter(modelId, key, "سلام")
                ProviderType.ANTHROPIC -> buildAnthropic(modelId, key, "سلام")
            }
            val reqBuilder = okhttp3.Request.Builder().url(url).post(body)
            headers.forEach { (k, v) -> reqBuilder.addHeader(k, v) }
            val res = client.newCall(reqBuilder.build()).execute()
            if (!res.isSuccessful) return Result.failure(Exception("HTTP ${res.code}: ${res.body?.string()}"))
            val txt = res.body?.string() ?: ""
            val content = parseTextFromResponse(txt)
            Result.success(content)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
    