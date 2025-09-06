package org.yarhooshmand.smartv3.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

fun scheduleReminder(ctx: Context, id: Long, timeMillis: Long, text: String) {
    val am = ctx.getSystemService(AlarmManager::class.java)
    val intent = Intent(ctx, ReminderReceiver::class.java).apply {
        putExtra("id", id)
        putExtra("text", text)
    }
    val pi = PendingIntent.getBroadcast(ctx, id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pi)
}
