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
    private var reminderId: Long = 0L

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val ch = "voice_listen"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(ch, "Voice Listening", NotificationManager.IMPORTANCE_LOW)
            channel.description = "شنود برای تایید یادآورها"
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        
        val notification: Notification = NotificationCompat.Builder(this, ch)
            .setContentTitle("🎤 گوش دادن برای تایید یادآور")
            .setContentText("بگویید: 'خوردم' یا 'انجام شد'")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .build()
            
        startForeground(3344, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        reminderId = intent?.getLongExtra("id", 0L) ?: 0L
        val reminderText = intent?.getStringExtra("text") ?: ""
        
        startListening()
        
        // Auto stop after 20 seconds
        job = GlobalScope.launch { 
            delay(20000L)
            stopSelf() 
        }
        
        return START_NOT_STICKY
    }

    private fun startListening() {
        try {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this)
            
            if (recognizer == null) {
                stopSelf()
                return
            }
            
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR") // فارسی
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000)
            }
            
            recognizer?.setRecognitionListener(object: RecognitionListener {
                override fun onResults(results: android.os.Bundle?) {
                    val texts = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: arrayListOf()
                    val allText = texts.joinToString(" ").lowercase()
                    
                    if (isConfirmationWord(allText)) {
                        markReminderDone()
                        showSuccessNotification()
                    }
                    stopSelf()
                }
                
                override fun onPartialResults(partialResults: android.os.Bundle?) {
                    val texts = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: arrayListOf()
                    val allText = texts.joinToString(" ").lowercase()
                    
                    if (isConfirmationWord(allText)) {
                        markReminderDone()
                        showSuccessNotification()
                        stopSelf()
                    }
                }
                
                override fun onReadyForSpeech(params: android.os.Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                
                override fun onError(error: Int) {
                    when (error) {
                        SpeechRecognizer.ERROR_NO_MATCH -> {
                            // Try again once
                            if (job?.isActive == true) {
                                recognizer?.startListening(intent)
                            }
                        }
                        else -> stopSelf()
                    }
                }
                
                override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
            })
            
            recognizer?.startListening(intent)
        } catch (e: Exception) {
            stopSelf()
        }
    }

    private fun isConfirmationWord(text: String): Boolean {
        val confirmWords = listOf(
            "خوردم", "انجام شد", "done", "taken", "خوردن",
            "تمام", "کردم", "yes", "بله", "آره", "اوکی",
            "ok", "انجام", "تایید", "بله انجام شد", "خورده"
        )
        return confirmWords.any { text.contains(it) }
    }

    private fun markReminderDone() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                AppDatabase.get(this@VoiceConfirmService).reminderDao().setDone(reminderId)
            } catch (e: Exception) {
                // Handle database error silently
            }
        }
    }

    private fun showSuccessNotification() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        val notification = NotificationCompat.Builder(this, "rem_channel")
            .setContentTitle("✅ تایید شد")
            .setContentText("یادآور به عنوان انجام شده علامت‌گذاری شد")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()
            
        notificationManager.notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        recognizer?.destroy()
        recognizer = null
        job?.cancel()
        job = null
    }
}