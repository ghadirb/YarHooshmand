package org.yarhooshmand.smartv3.reminders

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.yarhooshmand.smartv3.data.ReminderRepository

fun toggleReminderDone(context: Context, id: Long) {
    val repo = ReminderRepository.get(context)
    CoroutineScope(Dispatchers.IO).launch {
        repo.toggleDone(id)
    }
}
