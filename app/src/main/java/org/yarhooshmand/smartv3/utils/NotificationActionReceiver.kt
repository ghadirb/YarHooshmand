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
import org.yarhooshmand.smartv3.R
import org.yarhooshmand.smartv3.data.AppDatabase
import org.yarhooshmand.smartv3.data.ReminderEntity
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
            ACTION_SHOW -> {
                showNotification(context, id, text, time)
            }

            ACTION_MARK_DONE -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = AppDatabase.getInstance(context).reminderDao()
                    val entity = dao.getByIdOnce(id) ?: return@launch
                    val updated: ReminderEntity = entity.copy(
                        done = true,
                        completed = true,
                        completedAt = System.currentTimeMillis()
                    )
                    dao.update(updated)
                    // بستن نوتیفیکیشن
                    (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                        .cancel(notiIdFrom(id))
                }
            }

            ACTION_SNOOZE -> {
                // 10 دقیقه بعد
                val snoozeAt = System.currentTimeMillis() + 10 * 60 * 1000
                scheduleReminder(context, id, snoozeAt, if (text.isNotBlank()) text else "Reminder")
                // بستن نوتیفیکیشن
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .cancel(notiIdFrom(id))
            }
        }
    }

    private fun showNotification(context: Context, id: Long, text: String, time: Long) {
        ensureChannel(context)

        val markDoneIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_MARK_DONE
            putExtra(EXTRA_ID, id)
        }
        val markDonePi = PendingIntent.getBroadcast(
            context,
            (id * 10 + 1).toInt(),
            markDoneIntent,
            pendingFlags()
        )

        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_ID, id)
            putExtra(EXTRA_TEXT, text)
        }
        val snoozePi = PendingIntent.getBroadcast(
            context,
            (id * 10 + 2).toInt(),
            snoozeIntent,
            pendingFlags()
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // مطمئن شو این آیکن داری
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(text.ifBlank { "Reminder" })
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_done, // آیکن دلخواه
                context.getString(R.string.mark_done),
                markDonePi
            )
            .addAction(
                R.drawable.ic_snooze, // آیکن دلخواه
                context.ge
