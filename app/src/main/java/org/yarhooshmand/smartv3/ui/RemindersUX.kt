package org.yarhooshmand.smartv3.ui

import android.content.Context
import kotlinx.coroutines.flow.Flow
import org.yarhooshmand.smartv3.data.ReminderEntity
import org.yarhooshmand.smartv3.data.ReminderRepository
import org.yarhooshmand.smartv3.reminders.scheduleReminder

class RemindersUX(private val repository: ReminderRepository) {

    val reminders: Flow<List<ReminderEntity>> = repository.allReminders

    suspend fun addReminder(ctx: Context, text: String, timeMillis: Long, category: String? = null) {
        val reminder = ReminderEntity(text = text, timeMillis = timeMillis, category = category)
        val id = repository.insert(reminder)

        scheduleReminder(ctx, id, timeMillis, text)
    }

    suspend fun updateReminder(reminder: ReminderEntity) {
        repository.update(reminder)
    }

    suspend fun deleteReminder(reminder: ReminderEntity) {
        repository.delete(reminder)
    }
}
