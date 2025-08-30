
package org.yarhooshmand.smartv3.reminders

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import org.yarhooshmand.smartv3.data.AppDatabase

class VoiceConfirmService : Service() {
    private var recognizer: SpeechRecognizer? = null
    private var job: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val ch = "voice_listen"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(ch, "Voice Listening", NotificationManager.IMPORTANCE_LOW)
            )
        }
        val n: Notification = NotificationCompat.Builder(this, ch)
            .setContentTitle("گوش دادن برای تایید یادآور")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()
        startForeground(3344, n)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val reminderId = intent?.getLongExtra("id", 0L) ?: 0L
        listen(reminderId)
        return START_NOT_STICKY
    }

    private fun listen(reminderId: Long) {
        recognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        recognizer?.setRecognitionListener(object: RecognitionListener {
            override fun onResults(results: android.os.Bundle?) {
                val texts = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: arrayListOf()
                if (accept(texts.joinToString(" "))) {
                    GlobalScope.launch(Dispatchers.IO) {
                        AppDatabase.get(this@VoiceConfirmService).reminderDao().setDone(reminderId)
                    }
                }
                stopSelf()
            }
            override fun onPartialResults(partialResults: android.os.Bundle?) {}
            override fun onReadyForSpeech(params: android.os.Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) { stopSelf() }
            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        })
        recognizer?.startListening(i)
        job = GlobalScope.launch { delay(15000L); stopSelf() }
    }

    private fun accept(t: String): Boolean {
        val s = t.lowercase()
        return listOf("خوردم","انجام شد","done","taken","خوردن").any { s.contains(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        recognizer?.destroy()
        job?.cancel()
    }
}
