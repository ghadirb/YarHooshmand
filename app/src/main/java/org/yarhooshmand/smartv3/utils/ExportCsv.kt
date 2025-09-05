
package org.yarhooshmand.smartv3.utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import org.json.JSONArray
import org.json.JSONObject

object ExportCsv {
    fun exportReminders(ctx: Context, remindersJson: String): String? {
        return try {
            // remindersJson expected to be JSON array of reminders or simple '|' separated etc.
            val file = File(ctx.cacheDir, "reminders_export.csv")
            val fos = FileOutputStream(file)
            fos.write("id,text,timeMillis,category,smsTargets,done\n".toByteArray())
            // naive parse: try JSON array
            val arr = try { JSONArray(remindersJson) } catch (_: Exception) { null }
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val line = "${'$'}{o.optLong("id")},\"${'$'}{o.optString("text").replace("\"","'")}\",${'$'}{o.optLong("timeMillis")},\"${'$'}{o.optString("category")}\",\"${'$'}{o.optString("smsTargets")}\",${'$'}{o.optBoolean("done")}\n"
                    fos.write(line.toByteArray())
                }
            }
            fos.close()
            file.absolutePath
        } catch (_: Exception) { null }
    }
}
