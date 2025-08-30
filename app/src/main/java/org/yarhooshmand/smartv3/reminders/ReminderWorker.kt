
package org.yarhooshmand.smartv3.reminders

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.yarhooshmand.smartv3.models.ModelManager
import android.content.Intent

class ReminderWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val id = inputData.getLong("id", 0L)
        val text = inputData.getString("text") ?: "یادآور"
        val notif = NotificationCompat.Builder(applicationContext, "rem_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("یادآور")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(applicationContext).notify((System.currentTimeMillis()%Int.MAX_VALUE).toInt(), notif)

        if (ModelManager.isListenAfterAlarm(applicationContext)) {
            val s = Intent(applicationContext, VoiceConfirmService::class.java).apply {
                putExtra("id", id); putExtra("text", text)
            }
            applicationContext.startService(s)
        }
        return Result.success()
    }
}
