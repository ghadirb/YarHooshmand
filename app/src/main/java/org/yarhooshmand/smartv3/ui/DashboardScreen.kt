package org.yarhooshmand.smartv3.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import org.yarhooshmand.smartv3.utils.GoogleDriveHelper
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Composable
fun DashboardScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // Launchers for SAF
    val backupLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        val uri = res.data?.data ?: return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            // generate a small zip of DB (on-demand for manual backup)
            val baos = ByteArrayOutputStream()
            ZipOutputStream(baos).use { zos ->
                try {
                    val dbFile = ctx.getDatabasePath("yar_db")
                    if (dbFile.exists()) {
                        zos.putNextEntry(ZipEntry("databases/yar_db"))
                        dbFile.inputStream().use { it.copyTo(zos) }
                        zos.closeEntry()
                    }
                } catch (_: Exception) {}
            }
            GoogleDriveHelper.writeBackupToUri(ctx, uri, baos.toByteArray())
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        val uri = res.data?.data ?: return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            val data = GoogleDriveHelper.readBackupFromUri(ctx, uri) ?: return@launch
            // VERY SIMPLE restore: if zip contains databases/yar_db -> replace current DB
            try {
                val zis = java.util.zip.ZipInputStream(ByteArrayInputStream(data))
                var entry = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory && entry.name == "databases/yar_db") {
                        val out = ctx.getDatabasePath("yar_db")
                        out.outputStream().use { zis.copyTo(it) }
                        break
                    }
                    entry = zis.nextEntry
                }
                zis.close()
            } catch (_: Exception) { }
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("داشبورد", style = MaterialTheme.typography.headlineSmall)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { // manual backup
                val intent = GoogleDriveHelper.createBackupIntent("yar_backup.zip")
                backupLauncher.launch(intent)
            }) { Text("بک‌آپ دستی به درایو/حافظه") }

            Button(onClick = { // manual restore
                val intent = GoogleDriveHelper.createRestoreIntent()
                restoreLauncher.launch(intent)
            }) { Text("بازیابی بک‌آپ") }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                GoogleDriveHelper.scheduleAutoBackup(ctx, 1)
            }) { Text("فعال‌سازی بک‌آپ خودکار روزانه") }

            Button(onClick = {
                androidx.work.WorkManager.getInstance(ctx).cancelUniqueWork("yar_auto_backup")
            }) { Text("غیرفعال کردن بک‌آپ خودکار") }
        }
    }
}
