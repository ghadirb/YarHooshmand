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
        val id = intent.getLongExtra("id", -1L)
        val action = intent.getStringExtra("action") ?: ""
        if (id <= 0L) return

        val dao = ReminderDatabase.getInstance(context).reminderDao()
        CoroutineScope(Dispatchers.IO).launch {
            when (action) {
                "MARK_DONE" -> dao.markCompleted(id)
                // "DELETE" -> dao.delete(...)
            }
        }
    }
}
