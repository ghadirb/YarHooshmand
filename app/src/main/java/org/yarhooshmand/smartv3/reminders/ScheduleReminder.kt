package org.yarhooshmand.smartv3.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import org.yarhooshmand.smartv3.utils.NotificationActionReceiver

fun scheduleReminder(ctx: Context, id: Long, timeMillis: Long, text: String) {
    val alarmMgr = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(ctx, NotificationActionReceiver::class.java).apply {
        action = NotificationActionReceiver.ACTION_SHOW
        putExtra(NotificationActionReceiver.EXTRA_ID, id)
        putExtra(NotificationActionReceiver.EXTRA_TEXT, text)
        putExtra(NotificationActionReceiver.EXTRA_TIME, timeMillis)
    }
    val flags = PendingIntent.FLAG_UPDATE_CURRENT or
        (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
    val pi = PendingIntent.getBroadcast(ctx, id.toInt(), intent, flags)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pi)
    } else {
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, timeMillis, pi)
    }
}
