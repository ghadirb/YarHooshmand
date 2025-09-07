package org.yarhooshmand.smartv3.net

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.yarhooshmand.smartv3.ai.AIService
import org.yarhooshmand.smartv3.keys.KeysManager

object ChatClient {

    /**
     * Test model by calling AIService.chatOnce from IO dispatcher.
     * suspend so caller must call from coroutine (LaunchedEffect, lifecycleScope, etc.)
     */
    suspend fun testModel(ctx: Context, prompt: String = "ping"): String =
        withContext(Dispatchers.IO) {
            val key = KeysManager.getActiveKeySafe(ctx) ?: return@withContext "NO_KEY"
            AIService.chatOnce(prompt)
        }

    /**
     * Send prompt and get response. Suspend function.
     */
    suspend fun send(ctx: Context, prompt: String): String =
        withContext(Dispatchers.IO) {
            AIService.chatOnce(prompt)
        }

    fun getActiveKey(ctx: Context): String? = KeysManager.getActiveKey(ctx)
    fun getActiveKeySafe(ctx: Context): String? = KeysManager.getActiveKeySafe(ctx)
    fun reportBadKeyInternal(ctx: Context, reason: String) = KeysManager.reportBadKeyInternal(ctx, reason)
}
