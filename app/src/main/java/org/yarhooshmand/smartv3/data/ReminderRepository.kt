package org.yarhooshmand.smartv3.data

import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val dao: ReminderDao) {

    val allReminders: Flow<List<ReminderEntity>> = dao.getAllReminders()

    suspend fun insert(reminder: ReminderEntity): Long = dao.insert(reminder)

    suspend fun update(reminder: ReminderEntity) = dao.update(reminder)

    suspend fun delete(reminder: ReminderEntity) = dao.delete(reminder)

    fun getById(id: Long): Flow<ReminderEntity?> = dao.getReminderById(id)

    suspend fun markCompleted(id: Long) = dao.markCompleted(id)
}
