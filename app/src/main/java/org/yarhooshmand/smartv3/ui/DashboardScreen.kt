package org.yarhooshmand.smartv3.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.yarhooshmand.smartv3.data.ReminderEntity
import org.yarhooshmand.smartv3.utils.BackupUtils
import org.yarhooshmand.smartv3.utils.BackupWorker
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

@Composable
fun DashboardScreen() {
    val ctx = LocalContext.current
    Text("Dashboard")
}

fun createBackupIntent(): Intent =
    Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "application/json"
        putExtra(Intent.EXTRA_TITLE, "reminders_backup.json")
    }

fun createRestoreIntent(): Intent =
    Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "application/json"
    }

suspend fun writeBackupToUri(context: Context, uri: Uri): Boolean = withContext(Dispatchers.IO) {
    val list: List<ReminderEntity> = BackupUtils.exportBackup(context)
    val json = buildString {
        append("[")
        list.forEachIndexed { idx, r ->
            if (idx > 0) append(",")
            append("{")
            append(""id":").append(r.id).append(",")
            append(""text":").append(jsonEscape(r.text)).append(",")
            append(""timeMillis":").append(r.timeMillis).append(",")
            append(""category":").append(jsonEscape(r.category)).append(",")
            append(""done":").append(r.done).append(",")
            append(""completed":").append(r.completed).append(",")
            append(""completedAt":").append(r.completedAt ?: "null").append(",")
            append(""smsTargets":").append("[").append(r.smsTargets.joinToString(",") { jsonEscape(it) }).append("]")
            append("}")
        }
        append("]")
    }
    context.contentResolver.openOutputStream(uri)?.use { os ->
        OutputStreamWriter(os).use { it.write(json); it.flush() }
    } != null
}

suspend fun readBackupFromUri(context: Context, uri: Uri): Boolean = withContext(Dispatchers.IO) {
    val sb = StringBuilder()
    context.contentResolver.openInputStream(uri)?.use { `is` ->
        BufferedReader(InputStreamReader(`is`)).useLines { lines ->
            lines.forEach { sb.append(it) }
        }
    } ?: return@withContext false
    true
}

fun scheduleAutoBackup(context: Context) {
    val req = PeriodicWorkRequestBuilder<BackupWorker>(java.time.Duration.ofHours(24))
        .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
        .build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "auto_backup_work",
        ExistingPeriodicWorkPolicy.UPDATE,
        req
    )
}

private fun jsonEscape(s: String?): String =
    if (s == null) "null" else """ + s.replace("\", "\\").replace(""", "\"") + """
