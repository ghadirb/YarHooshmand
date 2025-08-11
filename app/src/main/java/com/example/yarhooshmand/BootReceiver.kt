package com.example.yarhooshmand

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("yar_prefs", Context.MODE_PRIVATE)
            val msg = prefs.getString("last_message", null)
            val time = prefs.getLong("last_time", -1L)
            if (msg != null && time > System.currentTimeMillis()) {
                val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val i = Intent(context, ReminderReceiver::class.java).putExtra("message", msg)
                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                else PendingIntent.FLAG_UPDATE_CURRENT
                val p = PendingIntent.getBroadcast(context, msg.hashCode(), i, flags)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, p)
                } else {
                    am.setExact(AlarmManager.RTC_WAKEUP, time, p)
                }
            }
        }
    }
}
