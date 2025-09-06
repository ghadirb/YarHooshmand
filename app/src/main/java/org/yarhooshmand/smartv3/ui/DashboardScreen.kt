package org.yarhooshmand.smartv3.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Constraints
import org.yarhooshmand.smartv3.utils.BackupWorker
import java.time.Duration

@Composable
fun DashboardScreen() {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = {
            // Activity should create an intent via createBackupIntent() and launch it to create a file.
            // Keeping UI simple here.
        }) {
            Text("Create Backup")
        }

        Button(onClick = {
            // Activity should open restore flow.
        }) {
            Text("Restore Backup")
        }

        Button(onClick = {
            scheduleAutoBackup(context)
        }) {
            Text("Schedule Auto Backup")
        }
    }
}

/** schedule a periodic 24-hour backup work (uses BackupWorker implemented in utils) */
fun scheduleAutoBackup(context: Context) {
    try {
        val req = PeriodicWorkRequestBuilder<BackupWorker>(Duration.ofHours(24))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "auto_backup_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            req
        )
    } catch (_: Throwable) {
        // On older Android API levels java.time.Duration may not be available; ignore scheduling failure.
    }
}
