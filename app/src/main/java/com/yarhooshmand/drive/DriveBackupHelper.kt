package com.yarhooshmand.drive

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.api.client.http.FileContent
import java.io.FileOutputStream
import java.util.Collections

class DriveBackupHelper(private val context: Context) {

    private fun buildService(): Drive? {
        val account = GoogleSignIn.getLastSignedInAccount(context)?.account ?: return null
        val credential = GoogleAccountCredential.usingOAuth2(context, Collections.singleton(DriveScopes.DRIVE_FILE))
        credential.selectedAccount = account
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()
        return Drive.Builder(transport, jsonFactory, credential)
            .setApplicationName("YarHooshmand")
            .build()
    }

    fun uploadJson(filePath: java.io.File): String? {
        val service = buildService() ?: return null
        val metadata = File()
        metadata.name = filePath.name
        val mediaContent = FileContent("application/json", filePath)
        val created = service.files().create(metadata, mediaContent)
            .setFields("id, name, createdTime")
            .execute()
        return created.id
    }

    fun listBackups(): List<File> {
        val service = buildService() ?: return emptyList()
        val result = service.files().list()
            .setQ("mimeType='application/json' and name contains 'backup_'")
            .setFields("files(id, name, createdTime)")
            .setOrderBy("createdTime desc")
            .execute()
        return result.files ?: emptyList()
    }

    fun downloadFile(fileId: String, dest: java.io.File): Boolean {
        val service = buildService() ?: return false
        FileOutputStream(dest).use { out ->
            service.files().get(fileId).executeMediaAndDownloadTo(out)
        }
        return true
    }

    fun downloadLatest(dest: java.io.File): Boolean {
        val items = listBackups()
        val latest = items.firstOrNull() ?: return false
        return downloadFile(latest.id, dest)
    }
}
