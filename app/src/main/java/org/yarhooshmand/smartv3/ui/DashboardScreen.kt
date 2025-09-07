package org.yarhooshmand.smartv3.ui

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf<String?>(null) }

    val createBackupLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
            uri?.let {
                scope.launch {
                    BackupUtils.writeBackupToUri(context, it)
                    status = "بکاپ ذخیره شد"
                }
            }
        }

    val restoreBackupLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                scope.launch {
                    BackupUtils.readBackupFromUri(context, it)
                    status = "بکاپ بازیابی شد"
                }
            }
        }

    Scaffold(topBar = { TopAppBar(title = { Text("داشبورد") }) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = { createBackupLauncher.launch("reminders_backup.json") }) { Text("ذخیره بکاپ") }
            Button(onClick = { restoreBackupLauncher.launch(arrayOf("application/json")) }) { Text("بازیابی بکاپ") }
            Button(onClick = {
                BackupWorker.scheduleAutoBackup(context)
                status = "بکاپ خودکار زمان‌بندی شد"
            }) { Text("فعال‌سازی بکاپ خودکار") }

            status?.let { Text(it) }
        }
    }
}
