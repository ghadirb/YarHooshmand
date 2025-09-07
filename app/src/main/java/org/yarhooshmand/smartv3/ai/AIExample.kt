package org.yarhooshmand.smartv3.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.yarhooshmand.smartv3.YarApp

/**
 * Small example helper that demonstrates using AIService.chatOnce from a coroutine.
 * It's a suspend function so callers must call it from a coroutine (e.g. LaunchedEffect or lifecycleScope).
 */
object AIExample {

    suspend fun runExample(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            // ensure app context available
            val ctx = YarApp.context() // may throw if YarApp not configured
            AIService.chatOnce(prompt)
        } catch (t: Throwable) {
            "EXAMPLE_ERROR: ${t.message}"
        }
    }
}
