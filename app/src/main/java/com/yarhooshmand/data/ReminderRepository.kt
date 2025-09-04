package com.yarhooshmand.data

import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val dao: ReminderDao) {
    val allReminders: Flow<List<Reminder>> = dao.getAllReminders()
    suspend fun insert(reminder: Reminder): Long = dao.insert(reminder)
    suspend fun update(reminder: Reminder) = dao.update(reminder)
    suspend fun delete(reminder: Reminder) = dao.delete(reminder)
    suspend fun deleteAll() = dao.deleteAll()
}
