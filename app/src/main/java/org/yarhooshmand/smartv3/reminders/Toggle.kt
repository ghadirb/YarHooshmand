package org.yarhooshmand.smartv3.reminders

import org.yarhooshmand.smartv3.data.ReminderEntity
import org.yarhooshmand.smartv3.data.ReminderRepository

suspend fun toggleDone(repo: ReminderRepository, reminder: ReminderEntity) {
    val updated = reminder.copy(done = !reminder.done)
    repo.update(updated)
}
