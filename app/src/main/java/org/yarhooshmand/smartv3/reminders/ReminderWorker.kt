package org.yarhooshmand.smartv3.reminders

import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.yarhooshmand.smartv3.MainActivity
import org.yarhooshmand.smartv3.models.ModelManager

class ReminderWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    
    override suspend fun doWork(): Result {
        val id = inputData.getLong("id", 0L)
        val text = inputData.getString("text") ?: "یادآور"
        
        // Create intent to open app when notification clicked
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 
            id.toInt(), 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create enhanced notification
        val notification = NotificationCompat.Builder(applicationContext, "rem_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🔔 یادآور هوشمند")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
            .build()

        try {
            NotificationManagerCompat.from(applicationContext)
                .notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification)
        } catch (e: SecurityException) {
            // Handle notification permission not granted
            return Result.failure()
        }

        // Start voice confirmation service if enabled
        if (ModelManager.isListenAfterAlarm(applicationContext)) {
            try {
                val serviceIntent = Intent(applicationContext, VoiceConfirmService::class.java).apply {
                    putExtra("id", id)
                    putExtra("text", text)
                }
                
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    applicationContext.startForegroundService(serviceIntent)
                } else {
                    applicationContext.startService(serviceIntent)
                }
            } catch (e: Exception) {
                // Handle service start error silently
            }
        }
        
        return Result.success()
    }
}