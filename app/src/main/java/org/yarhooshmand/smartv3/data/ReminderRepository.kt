package org.yarhooshmand.smartv3.data

import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val reminderDao: ReminderDao) {

    // لیست همه‌ی reminder ها
    val allReminders: Flow<List<ReminderEntity>> = reminderDao.getAllReminders()

    // افزودن reminder
    suspend fun insert(reminder: ReminderEntity): Long {
        return reminderDao.insert(reminder)
    }

    // حذف reminder
    suspend fun delete(reminder: ReminderEntity) {
        reminderDao.delete(reminder)
    }

    // آپدیت reminder
    suspend fun update(reminder: ReminderEntity) {
        reminderDao.update(reminder)
    }

    // گرفتن reminder بر اساس id
    fun getReminderById(id: Long): Flow<ReminderEntity?> {
        return reminderDao.getReminderById(id)
    }
}
