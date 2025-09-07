package org.yarhooshmand.smartv3.backup

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.yarhooshmand.smartv3.data.ReminderDatabase
import org.yarhooshmand.smartv3.data.ReminderEntity

object BackupUtils {

    suspend fun writeBackupToUri(ctx: Context, uri: Uri) = withContext(Dispatchers.IO) {
        val dao = ReminderDatabase.get(ctx).reminderDao()
        val list = dao.getAll().firstOrNull() ?: emptyList()
        val json = JSONArray()
        for (r in list) {
            val o = JSONObject()
            o.put("id", r.id)
            o.put("title", r.title)
            o.put("note", r.note)
            o.put("date", r.date)
            o.put("done", r.done)
            o.put("completed", r.completed)
            o.put("completedAt", r.completedAt)
            json.put(o)
        }
        ctx.contentResolver.openOutputStream(uri)?.use { os ->
            os.write(json.toString(2).toByteArray())
        }
    }

    suspend fun readBackupFromUri(ctx: Context, uri: Uri) = withContext(Dispatchers.IO) {
        val jsonStr = ctx.contentResolver.openInputStream(uri)?.use { it.readBytes().decodeToString() } ?: return@withContext
        val arr = JSONArray(jsonStr)
        val dao = ReminderDatabase.get(ctx).reminderDao()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val entity = ReminderEntity(
                id = o.optLong("id", 0),
                title = o.optString("title", ""),
                note = if (o.isNull("note")) null else o.optString("note"),
                date = if (o.isNull("date")) null else o.optLong("date"),
                done = o.optBoolean("done", false),
                completed = o.optBoolean("completed", false),
                completedAt = if (o.isNull("completedAt")) null else o.optLong("completedAt")
            )
            dao.upsert(entity)
        }
    }
}
