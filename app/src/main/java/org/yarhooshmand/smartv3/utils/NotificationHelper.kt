
package org.yarhooshmand.smartv3.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.yarhooshmand.smartv3.R

object NotificationHelper {
    const val CHANNEL_ID = "hooman_alerts"
    fun createChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Hooman Alerts"
            val desc = "Notifications for reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val ch = NotificationChannel(CHANNEL_ID, name, importance)
            ch.description = desc
            val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(ch)
        }
    }

    fun showReminderNotification(ctx: Context, id: Long, title: String, text: String) {
        createChannel(ctx)
        val takeIntent = Intent(ctx, NotificationActionReceiver::class.java).apply {
            action = "ACTION_TAKEN"
            putExtra("reminder_id", id)
        }
        val takePending = PendingIntent.getBroadcast(ctx, id.toInt(), takeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .addAction(android.R.drawable.checkbox_on_background, "خوردم", takePending)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(ctx)) {
            notify(id.toInt(), builder.build())
        }
    }
}
