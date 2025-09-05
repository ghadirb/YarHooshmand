
package org.yarhooshmand.smartv3.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.yarhooshmand.smartv3.data.ReminderDatabase
import org.yarhooshmand.smartv3.data.Reminder

class NotificationActionReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val id = intent.getLongExtra("reminder_id", 0L)
        Log.d("NotifReceiver", "action=$action id=$id")
        if (action == "ACTION_TAKEN") {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = ReminderDatabase.getInstance(context)
                    val dao = db.reminderDao()
                    val r = dao.findById(id) ?: return@launch
                    r.completed = true
                    r.completedAt = System.currentTimeMillis()
                    dao.update(r)
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }
}
