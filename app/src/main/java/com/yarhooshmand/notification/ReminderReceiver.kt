package com.yarhooshmand.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yarhooshmand.data.ReminderDatabase
import com.yarhooshmand.sms.SmsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        if (action == NotificationHelper.ACTION_MARK_DONE) {
            val id = intent.getIntExtra("reminder_id", -1)
            if (id != -1) {
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = ReminderDatabase.getDatabase(context).reminderDao()
                    val list = dao.getAllReminders().first()
                    list.find { it.id == id }?.let { dao.update(it.copy(isDone = true)) }
                }
            }
            return
        }

        val title = intent?.getStringExtra("title") ?: "یادآور"
        val time = intent?.getStringExtra("time") ?: ""
        val id = intent?.getIntExtra("reminder_id", (System.currentTimeMillis() % Int.MAX_VALUE).toInt()) ?: 0

        NotificationHelper.show(context, "یادآور: $title", "زمان: $time", id)

        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val smsEnabled = prefs.getBoolean("sms_enabled", false)
        val primary = prefs.getString("phone_number", "")
        val secondaryEnabled = prefs.getBoolean("sms_secondary_enabled", false)
        val secondary = prefs.getString("phone_number_secondary", "")

        if (smsEnabled && !primary.isNullOrEmpty()) {
            SmsHelper.send(primary, "یادآور: $title - زمان: $time")
        }
        if (secondaryEnabled && !secondary.isNullOrEmpty()) {
            SmsHelper.send(secondary, "یادآور (اشتراک): $title - زمان: $time")
        }
    }
}
