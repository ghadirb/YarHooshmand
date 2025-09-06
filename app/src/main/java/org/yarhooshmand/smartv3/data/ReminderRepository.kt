package org.yarhooshmand.smartv3.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class ReminderRepository private constructor(private val reminderDao: ReminderDao) {

    // لیست زنده (Flow)
    fun getAllReminders(): Flow<List<ReminderEntity>> {
        return reminderDao.getAllReminders()
    }

    // گرفتن همه یادآورها یکجا (برای جاهایی که Flow لازم نیست)
    suspend fun getAllOnce(): List<ReminderEntity> {
        return reminderDao.getAllOnce()
    }

    // افزودن یادآور
    suspend fun insert(reminder: ReminderEntity) {
        reminderDao.insert(reminder)
    }

    // بروزرسانی یادآور
    suspend fun update(reminder: ReminderEntity) {
        reminderDao.update(reminder)
    }

    // حذف یادآور
    suspend fun delete(reminder: ReminderEntity) {
        reminderDao.delete(reminder)
    }

    companion object {
        @Volatile
        private var INSTANCE: ReminderRepository? = null

        fun getInstance(context: Context): ReminderRepository {
            return INSTANCE ?: synchronized(this) {
                val database = AppDatabase.getDatabase(context)
                val instance = ReminderRepository(database.reminderDao())
                INSTANCE = instance
                instance
            }
        }
    }
}
