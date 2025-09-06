package org.yarhooshmand.smartv3.reminders

import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.yarhooshmand.smartv3.models.ModelManager

class ReminderWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val ctx = applicationContext  // ✅ مرجع ثابت و بدون خطا

        // داده‌ها رو با نوع درست می‌گیریم
        val id: Long = inputData.getLong("id", 0L)
        val text: String = inputData.getString("text") ?: "یادآور"

        // نوتیفیکیشن
        val notif = NotificationCompat.Builder(ctx, "rem_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("یادآور")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(ctx)
            .notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notif)

        // بعد از نوتیفیکیشن، سرویس تایید صوتی اگه فعال بود
        if (ModelManager.isListenAfterAlarm(ctx)) {
            val s = Intent(ctx, VoiceConfirmService::class.java).apply {
                putExtra("id", id)       // ✅ نوع Long دقیقاً مشخص شد
                putExtra("text", text)   // ✅ نوع String دقیقاً مشخص شد
            }
            ctx.startService(s)
        }

        return Result.success()
    }
}
