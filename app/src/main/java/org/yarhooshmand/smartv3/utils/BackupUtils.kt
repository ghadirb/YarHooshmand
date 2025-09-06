package org.yarhooshmand.smartv3.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.yarhooshmand.smartv3.data.Reminder
import org.yarhooshmand.smartv3.data.ReminderDatabase

object BackupUtils {

    suspend fun exportCsvToDownloads(context: Context): Result<Uri> = withContext(Dispatchers.IO) {
        return@withContext try {
            val dao = ReminderDatabase.get(context).reminderDao()
            val list = dao.getAll()  // Flow, need snapshot; create a once query
            // quick snapshot: use Room query alternative (add new function) — fallback simple empty
            Result.failure(IllegalStateException("Add a DAO query to fetch snapshot for export."))
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    fun toCsvRows(items: List<Reminder>): List<List<String>> =
        items.map { r ->
            listOf(
                r.id.toString(),
                r.title,
                r.note ?: "",
                r.date.toString(),
                r.done.toString(),
                r.completed.toString(),
                (r.completedAt ?: 0L).toString()
            )
        }
}
