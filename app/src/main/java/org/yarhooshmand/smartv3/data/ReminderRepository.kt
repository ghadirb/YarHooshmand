package org.yarhooshmand.smartv3.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class ReminderRepository private constructor(context: Context) {
    private val dao: ReminderDao = AppDatabase.getInstance(context).reminderDao()

    val allReminders: Flow<List<ReminderEntity>> = dao.getAll()

    fun getReminderById(id: Long) = dao.getReminderById(id)
    suspend fun getByIdOnce(id: Long) = dao.getByIdOnce(id)
    suspend fun getAllOnce() = dao.getAllOnce()
    suspend fun insert(reminder: ReminderEntity): Long = dao.insert(reminder)
    suspend fun insertAll(reminders: List<ReminderEntity>) = dao.insertAll(reminders)
    suspend fun update(reminder: ReminderEntity) = dao.update(reminder)
    suspend fun delete(reminder: ReminderEntity) = dao.delete(reminder)

    companion object {
        @Volatile private var INSTANCE: ReminderRepository? = null
        fun getInstance(context: Context): ReminderRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ReminderRepository(context.applicationContext).also { INSTANCE = it }
            }
    }
}
