package org.yarhooshmand.smartv3.ai

import android.content.Context
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException

class AIService(private val context: Context) {

    private val client = OkHttpClient()

    suspend fun chat(message: String): String {
        val mediaType = "application/json".toMediaTypeOrNull()
        val body = RequestBody.create(mediaType, """{"input":"$message"}""")

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }
            return response.body?.string() ?: ""
        }
    }
}
