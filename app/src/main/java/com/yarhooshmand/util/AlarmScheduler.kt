package com.yarhooshmand.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.*

object AlarmScheduler {
    fun schedule(context: Context, date: String, time: String, title: String, reminderId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent().setClassName(context, "com.yarhooshmand.notification.ReminderReceiver").apply {
            putExtra("title", title)
            putExtra("time", time)
            putExtra("reminder_id", reminderId)
        }
        val pi = PendingIntent.getBroadcast(context, reminderId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val triggerAt = sdf.parse("$date $time")?.time ?: System.currentTimeMillis() + 1000
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
    }
}
