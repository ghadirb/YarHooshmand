package org.yarhooshmand.smartv3.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.yarhooshmand.smartv3.data.ReminderDatabase
import org.yarhooshmand.smartv3.data.ReminderEntity
import org.json.JSONArray
import org.json.JSONObject

object BackupUtils {

    suspend fun exportToJson(context: Context): String = withContext(Dispatchers.IO) {
        val dao = ReminderDatabase.getInstance(context).reminderDao()
        val list: List<ReminderEntity> = dao.getAll().first()
        val arr = JSONArray()
        list.forEach { r ->
            val o = JSONObject()
            o.put("id", r.id)
            o.put("text", r.text)
            o.put("timeMillis", r.timeMillis)
            o.put("category", r.category)
            o.put("smsTargets", r.smsTargets)
            o.put("done", r.done)
            arr.put(o)
        }
        arr.toString()
    }

    // نسخهٔ بی‌سروصدا؛ پیاده‌سازی واقعی آپلود را اینجا اضافه کن
    suspend fun tryUploadBackupSilent(context: Context) {
        // TODO: ارسال arr به سرور/فضای ابری موردنظر
        exportToJson(context) // تا فعلاً فقط JSON تولید شود
    }
}
