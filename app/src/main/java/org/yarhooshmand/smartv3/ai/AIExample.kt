package org.yarhooshmand.smartv3.ai

import android.content.Context

object AIExample {
    fun quickTest(ctx: Context, prompt: String, onResult: (String) -> Unit, onError: (Throwable) -> Unit) {
        val svc = AIService(ctx)
        val res = svc.chat(prompt)
        if (res.isSuccess) onResult(res.getOrNull() ?: "") else onError(res.exceptionOrNull()!!)
    }
}
