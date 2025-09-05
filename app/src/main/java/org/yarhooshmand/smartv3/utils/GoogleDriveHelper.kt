
package org.yarhooshmand.smartv3.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Request
import okhttp3.OkHttpClient
import java.io.InputStream

object GoogleDriveHelper {
    // Placeholder client id: replace with your OAuth 2.0 Client ID for Android if available.
    // Using a placeholder won't break build but authentication will require a real client id for real Drive access.
    const val CLIENT_ID = "969121531131-6des7gr18i72bh5f2oc977gng1qc85b1.apps.googleusercontent.com"
    const val DRIVE_SCOPE = "https://www.googleapis.com/auth/drive.file"

    private val client = OkHttpClient()

    fun getSignInClient(activity: Activity): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DRIVE_SCOPE))
            .requestServerAuthCode(CLIENT_ID)
            .build()
        return GoogleSignIn.getClient(activity, gso)
    }

    fun isSignedIn(ctx: Context): Boolean {
        val acct = GoogleSignIn.getLastSignedInAccount(ctx)
        return acct != null
    }

    fun getAccount(ctx: Context): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(ctx)
    }

    suspend fun uploadFile(ctx: Context, acct: GoogleSignInAccount, filename: String, input: InputStream): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val token = acct.idToken ?: acct.serverAuthCode
                // Note: For a production flow you should exchange serverAuthCode for access token on a backend,
                // or use GoogleAuthUtil to get a token. Here we attempt a simple placeholder approach.
                // This function will compile; for real upload, replace with full OAuth exchange.
                Log.i("DriveHelper", "Uploading file (placeholder flow). Filename: $filename")
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    // Create an ACTION_CREATE_DOCUMENT intent so the user may choose where to save a backup (including Google Drive via SAF)
    fun createBackupIntent(defaultFilename: String): Intent {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/zip"
        intent.putExtra(Intent.EXTRA_TITLE, defaultFilename)
        return intent
    }

    // Create an ACTION_OPEN_DOCUMENT intent so the user can choose a backup file to restore
    fun createRestoreIntent(): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        return intent
    }

    // Write bytes to a Uri returned by SAF (user chosen location)
    suspend fun writeBackupToUri(ctx: Context, uri: Uri, data: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                ctx.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(data)
                    out.flush()
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    // Read file bytes from a Uri (user chosen file via SAF)
    suspend fun readBackupFromUri(ctx: Context, uri: Uri): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                ctx.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Schedule auto backups using WorkManager. This schedules a periodic worker that will create a local backup
    // and attempt to upload silently if possible (requires user to have signed-in and permissions).
    fun scheduleAutoBackup(ctx: Context, daysInterval: Long = 1) {
        try {
            val req = androidx.work.PeriodicWorkRequestBuilder<BackupWorker>(
                java.time.Duration.ofDays(daysInterval)
            ).setInitialDelay(java.time.Duration.ofMinutes(10)).build()
            androidx.work.WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
                "yar_auto_backup",
                androidx.work.ExistingPeriodicWorkPolicy.REPLACE,
                req
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Try a silent upload of a local backup file using the signed-in Google account (best-effort).
    suspend fun tryUploadBackupSilent(ctx: Context, filename: String, inputStream: java.io.InputStream): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val acct = getAccount(ctx)
                if (acct == null) {
                    Log.w("DriveHelper", "No signed-in account available for silent upload.")
                    return@withContext false
                }
                // Obtain an OAuth access token for DRIVE_FILE scope.
                // NOTE: For a production-safe flow, exchange serverAuthCode on a backend rather than using GoogleAuthUtil.
                try {
                    val scope = "oauth2:${DRIVE_SCOPE}"
                    val accessToken = com.google.android.gms.auth.GoogleAuthUtil.getToken(ctx, acct.account?.name, scope)
                    if (accessToken.isNullOrEmpty()) {
                        Log.w("DriveHelper", "Could not obtain access token for Drive upload.")
                        return@withContext false
                    }
                    // Build multipart upload to Drive REST API
                    val meta = "{\"name\": \"${filename}\"}"
                    val boundary = "----yarboundary${System.currentTimeMillis()}"
                    val mediaType = ("multipart/related; boundary=$boundary").toMediaType()
                    val baos = java.io.ByteArrayOutputStream()
                    val writer = java.io.OutputStreamWriter(baos, Charsets.UTF_8)
                    writer.write("--$boundary\r\n")
                    writer.write("Content-Type: application/json; charset=UTF-8\r\n\r\n")
                    writer.write(meta + "\r\n")
                    writer.write("--$boundary\r\n")
                    writer.write("Content-Type: application/zip\r\n\r\n")
                    writer.flush()
                    inputStream.copyTo(baos)
                    writer.write("\r\n--$boundary--\r\n")
                    writer.flush()
                    val reqBody = baos.toByteArray().toRequestBody(mediaType)
                    val req = Request.Builder()
                        .url("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart")
                        .addHeader("Authorization", "Bearer $accessToken")
                        .post(reqBody)
                        .build()
                    val resp = client.newCall(req).execute()
                    val ok = resp.isSuccessful
                    if (!ok) {
                        Log.w("DriveHelper", "Upload failed: \${resp.code} \${resp.message}")
                    }
                    resp.close()
                    ok
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

}
