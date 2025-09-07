package org.yarhooshmand.smartv3.backup

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class BackupWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        // Implement your background backup target here if desired.
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "auto_backup"
        fun scheduleAutoBackup(ctx: Context) {
            val req = PeriodicWorkRequestBuilder<BackupWorker>(24, TimeUnit.HOURS).build()
            WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                req
            )
        }
    }
}
