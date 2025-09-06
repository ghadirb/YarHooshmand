package org.yarhooshmand.smartv3.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.yarhooshmand.smartv3.backup.BackupUtils
import org.yarhooshmand.smartv3.backup.BackupWorker

@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var backupStatus by remember { mutableStateOf<String?>(null) }

    val createBackupLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
            uri?.let {
                scope.launch {
                    BackupUtils.writeBackupToUri(context, it)
                    backupStatus = "بکاپ با موفقیت ذخیره شد"
                }
            }
        }

    val restoreBackupLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                scope.launch {
                    BackupUtils.readBackupFromUri(context, it)
                    backupStatus = "بکاپ با موفقیت بازیابی شد"
                }
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("داشبورد") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { createBackupLauncher.launch("reminders_backup.json") }) {
                Text("ذخیره بکاپ")
            }

            Button(onClick = { restoreBackupLauncher.launch(arrayOf("application/json")) }) {
                Text("بازیابی بکاپ")
            }

            Button(onClick = {
                BackupWorker.scheduleAutoBackup(context)
                backupStatus = "بکاپ خودکار فعال شد"
            }) {
                Text("فعال‌سازی بکاپ خودکار")
            }

            backupStatus?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
