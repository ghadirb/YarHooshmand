package org.yarhooshmand.smartv3.net

import android.content.Context
import org.yarhooshmand.smartv3.ai.AIService
import org.yarhooshmand.smartv3.keys.KeysManager

object ChatClient {

    fun testModel(ctx: Context, prompt: String = "ping"): String {
        val key = KeysManager.getActiveKeySafe(ctx) ?: return "NO_KEY"
        return AIService.chatOnce(prompt)
    }

    fun send(ctx: Context, prompt: String): String = AIService.chatOnce(prompt)

    fun getActiveKey(ctx: Context): String? = KeysManager.getActiveKey(ctx)
    fun getActiveKeySafe(ctx: Context): String? = KeysManager.getActiveKeySafe(ctx)
    fun reportBadKeyInternal(ctx: Context, reason: String) = KeysManager.reportBadKeyInternal(ctx, reason)
}
