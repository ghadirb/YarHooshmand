package org.yarhooshmand.smartv3.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val dao: ReminderDao) {
    fun getAll(): Flow<List<ReminderEntity>> = dao.getAll()
    suspend fun insert(e: ReminderEntity): Long = dao.insert(e)
    suspend fun update(e: ReminderEntity) = dao.update(e)
    suspend fun delete(e: ReminderEntity) = dao.delete(e)
    suspend fun markCompleted(id: Long) = dao.markCompleted(id)
    fun getToday(): Flow<List<ReminderEntity>> = dao.getToday()
}

object ReminderRepositoryProvider {
    fun from(context: Context): ReminderRepository =
        ReminderRepository(ReminderDatabase.get(context).reminderDao())
}
