package org.yarhooshmand.smartv3.data

import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val dao: ReminderDao) {

    val allReminders: Flow<List<ReminderEntity>> = dao.getAll()

    fun getReminderById(id: Long): Flow<ReminderEntity?> = dao.getReminderById(id)

    suspend fun insert(reminder: ReminderEntity): Long = dao.insert(reminder)

    suspend fun update(reminder: ReminderEntity) = dao.update(reminder)

    suspend fun delete(reminder: ReminderEntity) = dao.delete(reminder)

    suspend fun insertAll(reminders: List<ReminderEntity>) = dao.insertAll(reminders)

    suspend fun getAllOnce(): List<ReminderEntity> = dao.getAllOnce()
}
