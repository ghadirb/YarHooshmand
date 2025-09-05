package org.yarhooshmand.smartv3.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

/**
 * BackupWorker
 * - Creates a zip containing the Room database and app files, placed in cacheDir
 * - If a signed-in Google account exists and permissions are available, attempts a silent upload
 */
class BackupWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val ctx = applicationContext
            val timestamp = System.currentTimeMillis()
            val outFile = File(ctx.cacheDir, "yar_backup_$timestamp.zip")
            ZipOutputStream(FileOutputStream(outFile)).use { zos ->
                // add database file if exists
                try {
                    val dbFile = ctx.getDatabasePath("yar_db")
                    if (dbFile.exists()) {
                        FileInputStream(dbFile).use { fis ->
                            zos.putNextEntry(ZipEntry("databases/yar_db"))
                            fis.copyTo(zos)
                            zos.closeEntry()
                        }
                    }
                } catch (e: Exception) {
                    Log.w("BackupWorker", "Failed to add DB to zip: ${e.message}")
                }
                // add files directory contents (small files only)
                try {
                    val filesDir = ctx.filesDir
                    filesDir.listFiles()?.forEach { f ->
                        if (f.isFile) {
                            FileInputStream(f).use { fis ->
                                zos.putNextEntry(ZipEntry("files/${f.name}"))
                                fis.copyTo(zos)
                                zos.closeEntry()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w("BackupWorker", "Failed to add files dir: ${e.message}")
                }
            }

            // Try silent upload (best-effort). If it fails, we still succeed to avoid repeated retries.
            try {
                FileInputStream(outFile).use { fis ->
                    val ok = GoogleDriveHelper.tryUploadBackupSilent(ctx, outFile.name, fis)
                    if (ok) {
                        Log.i("BackupWorker", "Silent upload succeeded for backup ${outFile.name}")
                    } else {
                        Log.i("BackupWorker", "Silent upload not available; backup saved locally: ${outFile.absolutePath}")
                    }
                }
            } catch (e: Exception) {
                Log.w("BackupWorker", "Silent upload attempt failed: ${e.message}")
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
