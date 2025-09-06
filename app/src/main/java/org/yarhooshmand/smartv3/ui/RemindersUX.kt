package org.yarhooshmand.smartv3.ui

import android.content.Context
import kotlinx.coroutines.flow.Flow
import org.yarhooshmand.smartv3.data.ReminderEntity
import org.yarhooshmand.smartv3.data.ReminderRepository
import org.yarhooshmand.smartv3.reminders.scheduleReminder

class RemindersUX(private val repo: ReminderRepository) {

    val reminders: Flow<List<ReminderEntity>> = repo.allReminders

    suspend fun addReminder(ctx: Context, text: String, timeMillis: Long, category: String? = null, smsTargets: List<String> = emptyList()) {
        val id = repo.insert(
            ReminderEntity(
                text = text,
                timeMillis = timeMillis,
                category = category,
                smsTargets = smsTargets
            )
        )
        scheduleReminder(ctx, id, timeMillis, text)
    }

    suspend fun updateReminder(reminder: ReminderEntity) = repo.update(reminder)

    suspend fun deleteReminder(reminder: ReminderEntity) = repo.delete(reminder)

    suspend fun markCompleted(id: Long) {
        val entity = repo.getByIdOnce(id) ?: return
        repo.update(entity.copy(done = true, completed = true, completedAt = System.currentTimeMillis()))
    }
}
