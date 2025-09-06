
package org.yarhooshmand.smartv3.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

object TtsManager {
    private var tts: TextToSpeech? = null
    fun init(ctx: Context) {
        if (tts != null) return
        tts = TextToSpeech(ctx.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("fa")
            }
        }
    }
    fun speak(ctx: Context, text: String) {
        try {
            if (tts == null) init(ctx)
            tts?.speak(text, TextToSpeech.QUEUE_ADD, null, null)
        } catch (_: Exception) {}
    }
    fun shutdown() { try { tts?.shutdown() } catch (_: Exception) {} }
}
