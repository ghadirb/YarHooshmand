package org.yarhooshmand.smartv3.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra("id", 0L)
        val text = intent.getStringExtra("text") ?: "یادآوری"
        val builder = NotificationCompat.Builder(context, "rem_channel")
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("یادآوری")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        NotificationManagerCompat.from(context).notify(id.toInt(), builder.build())

        // Optionally start voice confirm service if enabled
        context.startService(Intent(context, VoiceConfirmService::class.java).putExtra("id", id))
    }
}
