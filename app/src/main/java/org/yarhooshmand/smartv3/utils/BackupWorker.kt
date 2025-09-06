package org.yarhooshmand.smartv3.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.yarhooshmand.smartv3.data.ReminderEntity

class BackupWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        return try {
            val reminders: List<ReminderEntity> = BackupUtils.exportBackup(applicationContext)
            // TODO: ذخیره/آپلود بکاپ
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
