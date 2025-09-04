package com.yarhooshmand.backup

import android.content.Context
import android.net.Uri
import com.yarhooshmand.data.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

object ExportImport {
    suspend fun exportJson(context: Context, destUri: Uri, items: List<Reminder>) = withContext(Dispatchers.IO) {
        val json = JSONArray()
        items.forEach {
            json.put(JSONObject().apply {
                put("id", it.id); put("title", it.title); put("date", it.date); put("time", it.time); put("isDone", it.isDone)
            })
        }
        context.contentResolver.openOutputStream(destUri)?.use { it.write(json.toString(2).toByteArray()) }
    }

    suspend fun exportCsv(context: Context, destUri: Uri, items: List<Reminder>) = withContext(Dispatchers.IO) {
        val header = "id,title,date,time,isDone\n"
        val body = items.joinToString("\n") { "${'$'}{it.id},\"${'$'}{it.title}\",${'$'}{it.date},${'$'}{it.time},${'$'}{it.isDone}" }
        context.contentResolver.openOutputStream(destUri)?.use { it.write((header + body).toByteArray()) }
    }
}
