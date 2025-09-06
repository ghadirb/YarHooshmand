package org.yarhooshmand.smartv3.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.yarhooshmand.smartv3.data.ReminderEntity

class BackupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val reminders: List<ReminderEntity> =
                    BackupUtils.exportReminders(applicationContext)

                // 📤 اینجا می‌تونی آپلود بک‌آپ رو انجام بدی
                // مثلا: ApiClient.uploadBackup(reminders)

                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.retry()
            }
        }
    }
}
