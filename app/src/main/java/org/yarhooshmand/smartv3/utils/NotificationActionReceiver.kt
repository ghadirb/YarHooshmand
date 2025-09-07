package org.yarhooshmand.smartv3.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.yarhooshmand.smartv3.data.AppDatabase
import org.yarhooshmand.smartv3.reminders.scheduleReminder

class NotificationActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SHOW = "org.yarhooshmand.smartv3.ACTION_SHOW"
        const val ACTION_MARK_DONE = "org.yarhooshmand.smartv3.ACTION_MARK_DONE"
        const val ACTION_SNOOZE = "org.yarhooshmand.smartv3.ACTION_SNOOZE"

        const val EXTRA_ID = "extra_reminder_id"
        const val EXTRA_TEXT = "extra_reminder_text"
        const val EXTRA_TIME = "extra_reminder_time"

        private const val CHANNEL_ID = "reminders_channel"
        private const val CHANNEL_NAME = "Reminders"
        private const val NOTI_ID_BASE = 1000
    }

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra(EXTRA_ID, -1L)
        val text = intent.getStringExtra(EXTRA_TEXT) ?: ""
        val time = intent.getLongExtra(EXTRA_TIME, 0L)

        when (intent.action) {
            ACTION_SHOW -> showNotification(context, id, text, time)
            ACTION_MARK_DONE -> markDone(context, id)
            ACTION_SNOOZE -> snooze(context, id, text)
        }
    }

    private fun markDone(context: Context, id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getInstance(context).reminderDao()
            val entity = dao.getByIdOnce(id) ?: return@launch
            dao.update(entity.copy(done = true, completed = true, completedAt = System.currentTimeMillis()))
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .cancel(notiIdFrom(id))
        }
    }

    private fun snooze(context: Context, id: Long, text: String) {
        val snoozeAt = System.currentTimeMillis() + 10 * 60 * 1000
        scheduleReminder(context, id, snoozeAt, text.ifBlank { "Reminder" })
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .cancel(notiIdFrom(id))
    }

    private fun showNotification(context: Context, id: Long, text: String, time: Long) {
        ensureChannel(context)

        val doneIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_MARK_DONE
            putExtra(EXTRA_ID, id)
        }
        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_ID, id)
            putExtra(EXTRA_TEXT, text)
        }

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
            (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)

        val donePi = PendingIntent.getBroadcast(context, (id * 10 + 1).toInt(), doneIntent, flags)
        val snoozePi = PendingIntent.getBroadcast(context, (id * 10 + 2).toInt(), snoozeIntent, flags)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Reminder")
            .setContentText(text.ifBlank { "Reminder" })
            .setAutoCancel(true)
            .addAction(android.R.drawable.checkbox_on_background, "Mark done", donePi)
            .addAction(android.R.drawable.ic_menu_recent_history, "Snooze", snoozePi)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(notiIdFrom(id), builder.build())
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (mgr.getNotificationChannel(CHANNEL_ID) == null) {
                mgr.createNotificationChannel(
                    NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                )
            }
        }
    }

    private fun notiIdFrom(id: Long): Int = (NOTI_ID_BASE + (id % Int.MAX_VALUE)).toInt()
}
