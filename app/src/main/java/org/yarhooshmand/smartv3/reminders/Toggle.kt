package org.yarhooshmand.smartv3.reminders

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun reminderToggleDone(repo: ReminderRepository, r: Reminder) {
    CoroutineScope(Dispatchers.IO).launch {
        repo.update(r.copy(done = !r.done))
    }
}
