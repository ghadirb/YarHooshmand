package org.yarhooshmand.smartv3.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.yarhooshmand.smartv3.data.local.Reminder
import org.yarhooshmand.smartv3.data.local.ReminderDao
import org.yarhooshmand.smartv3.data.local.ReminderDatabase

class ReminderRepository private constructor(private val reminderDao: ReminderDao) {

    val allReminders: LiveData<List<Reminder>> = reminderDao.getAll()

    suspend fun insert(reminder: Reminder): Long {
        return withContext(Dispatchers.IO) {
            reminderDao.insert(reminder)
        }
    }

    suspend fun update(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            reminderDao.update(reminder)
        }
    }

    suspend fun delete(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            reminderDao.delete(reminder)
        }
    }

    suspend fun getByIdOnce(id: Long): Reminder? {
        return withContext(Dispatchers.IO) {
            reminderDao.getById(id)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ReminderRepository? = null

        fun getInstance(database: ReminderDatabase): ReminderRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = ReminderRepository(database.reminderDao())
                INSTANCE = instance
                instance
            }
        }
    }
}
