
package org.yarhooshmand.smartv3.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import org.yarhooshmand.smartv3.data.Reminder
import org.yarhooshmand.smartv3.data.ReminderDatabase

object BackupUtils {
    suspend fun exportRemindersToFile(ctx: Context): String? = withContext(Dispatchers.IO) {
        try {
            val db = ReminderDatabase.getInstance(ctx)
            val dao = db.reminderDao()
            val list = dao.getAllSync()
            val arr = JSONArray()
            for (r in list) {
                val o = JSONObject()
                o.put("id", r.id)
                o.put("title", r.title)
                o.put("time", r.time)
                o.put("completed", r.completed)
                o.put("completedAt", r.completedAt)
                arr.put(o)
            }
            val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
            val name = "hooman_backup_" + sdf.format(Date()) + ".json"
            val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloads.exists()) downloads.mkdirs()
            val out = File(downloads, name)
            out.writeText(arr.toString(2))
            return@withContext out.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    suspend fun importRemindersFromUri(ctx: Context, uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val input = ctx.contentResolver.openInputStream(uri) ?: return@withContext false
            val text = input.bufferedReader().use { it.readText() }
            val arr = JSONArray(text)
            val db = ReminderDatabase.getInstance(ctx)
            val dao = db.reminderDao()
            val list = mutableListOf<Reminder>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val r = Reminder()
                r.id = o.optLong("id", 0)
                r.title = o.optString("title", null)
                r.time = o.optString("time", null)
                r.completed = o.optBoolean("completed", false)
                r.completedAt = o.optLong("completedAt", 0)
                list.add(r)
            }
            dao.insertAll(list)
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
}
