package org.yarhooshmand.smartv3.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class BackupWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return try {
            BackupUtils.tryUploadBackupSilent(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
