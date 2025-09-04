package com.yarhooshmand.backup

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yarhooshmand.data.ReminderDatabase
import com.yarhooshmand.drive.DriveBackupHelper
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class AutoBackupWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val dao = ReminderDatabase.getDatabase(applicationContext).reminderDao()
        val items = dao.getAllReminders().first()
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        val dir = applicationContext.getExternalFilesDir(null) ?: return Result.failure()
        val file = java.io.File(dir, "backup_${'$'}{sdf.format(Date())}.json")
        file.writeText(items.joinToString(prefix = "[", postfix = "]") {
            "{"title":"${'$'}{it.title}","date":"${'$'}{it.date}","time":"${'$'}{it.time}","isDone":${'$'}{it.isDone}}"
        })

        try { DriveBackupHelper(applicationContext).uploadJson(file) } catch (_: Exception) {}
        return Result.success()
    }
}
