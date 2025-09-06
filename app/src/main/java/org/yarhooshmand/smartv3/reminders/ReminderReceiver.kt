package org.yarhooshmand.smartv3.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra("id", 0L)
        val text = intent.getStringExtra("text") ?: "یادآوری"
        val builder = NotificationCompat.Builder(context, "rem_channel")
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("یادآوری")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        NotificationManagerCompat.from(context).notify(id.toInt(), builder.build())

        // ارسال SMS در صورت فعال بودن
        try {
            val enabled = org.yarhooshmand.smartv3.utils.SmsPrefs.isEnabledForReminder(context, id)
            val master = org.yarhooshmand.smartv3.utils.SmsPrefs.isMasterEnabled(context)
            val number = org.yarhooshmand.smartv3.utils.SmsPrefs.getDefaultNumber(context)
            if (enabled && master && number.isNotBlank()) {
                val perm = androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.SEND_SMS)
                if (perm == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    val mgr = android.telephony.SmsManager.getDefault()
                    mgr.sendTextMessage(number, null, text, null, null)
                }
            }
        } catch (_: Exception) {}
    

        // Optionally start voice confirm service if enabled
        context.startService(Intent(context, VoiceConfirmService::class.java).putExtra("id", id))

        // ارسال SMS به چند شماره در صورت فعال بودن
        try {
            val master = org.yarhooshmand.smartv3.utils.SmsPrefs.isMasterEnabled(context)
            val defaultNum = org.yarhooshmand.smartv3.utils.SmsPrefs.getDefaultNumber(context)
            if (master) {
                val targets = mutableListOf<String>()
                // if reminder provided explicit targets in intent extras, use them
                val extraTargets = intent.getStringExtra("smsTargets")
                if (!extraTargets.isNullOrBlank()) {
                    extraTargets.split(',').map { it.trim() }.filter { it.isNotEmpty() }.forEach { targets.add(it) }
                }
                // add default if none
                if (targets.isEmpty() && defaultNum.isNotBlank()) targets.add(defaultNum)
                for (num in targets) {
                    try {
                        val perm = androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.SEND_SMS)
                        if (perm == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            val mgr = android.telephony.SmsManager.getDefault()
                            mgr.sendTextMessage(num, null, text, null, null)
                        }
                    } catch (_: Exception) {}
                }
            }
        } catch (_: Exception) {}

    }
}
