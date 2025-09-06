package org.yarhooshmand.smartv3.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.yarhooshmand.smartv3.data.AppDatabase
import org.yarhooshmand.smartv3.data.ReminderEntity

object BackupUtils {
    suspend fun exportBackup(context: Context): List<ReminderEntity> =
        withContext(Dispatchers.IO) {
            AppDatabase.getInstance(context).reminderDao().getAllOnce()
        }

    suspend fun importBackup(context: Context, reminders: List<ReminderEntity>) =
        withContext(Dispatchers.IO) {
            AppDatabase.getInstance(context).reminderDao().insertAll(reminders)
        }
}
