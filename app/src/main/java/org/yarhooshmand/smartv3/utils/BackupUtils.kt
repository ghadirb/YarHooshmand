package org.yarhooshmand.smartv3.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.yarhooshmand.smartv3.data.AppDatabase
import org.yarhooshmand.smartv3.data.ReminderEntity

object BackupUtils {

    /**
     * گرفتن بک‌آپ از تمام reminder ها
     */
    suspend fun exportReminders(context: Context): List<ReminderEntity> {
        return withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            db.reminderDao().getAllOnce() // نیاز داریم یک متد @Query ساده اضافه کنیم
        }
    }

    /**
     * وارد کردن reminder ها از بک‌آپ
     */
    suspend fun importReminders(context: Context, reminders: List<ReminderEntity>) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            db.reminderDao().insertAll(reminders)
        }
    }
}
