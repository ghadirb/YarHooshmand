
package org.yarhooshmand.smartv3.reminders

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

fun scheduleReminder(ctx: Context, id: Long, timeMillis: Long, text: String) {
    val delay = (timeMillis - System.currentTimeMillis()).coerceAtLeast(0L)
    val data = workDataOf("id" to id, "text" to text)
    val req = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .build()
    WorkManager.getInstance(ctx).enqueueUniqueWork("reminder_$id", ExistingWorkPolicy.REPLACE, req)
}
