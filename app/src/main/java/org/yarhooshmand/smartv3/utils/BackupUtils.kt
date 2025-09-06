package org.yarhooshmand.smartv3.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.yarhooshmand.smartv3.data.AppDatabase
import org.yarhooshmand.smartv3.data.ReminderEntity

class BackupWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return try {
            val dao = AppDatabase.getInstance(applicationContext).reminderDao()
            val reminders: List<ReminderEntity> = dao.getAllOnce()

            // TODO: sync reminders with server or local file
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
