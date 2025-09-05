package org.yarhooshmand.smartv3.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.yarhooshmand.smartv3.data.ReminderDatabase

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_MARK_DONE) {
            val id = intent.getIntExtra(EXTRA_REMINDER_ID, -1)
            if (id > 0) {
                CoroutineScope(Dispatchers.IO).launch {
                    ReminderDatabase.get(context).dao().markCompleted(id, System.currentTimeMillis())
                }
            }
        }
    }
    companion object {
        const val ACTION_MARK_DONE = "org.yarhooshmand.smartv3.ACTION_MARK_DONE"
        const val EXTRA_REMINDER_ID = "reminder_id"
    }
}
