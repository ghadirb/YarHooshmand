package org.yarhooshmand.smartv3.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // In a production app, reload persisted reminders and reschedule here.
        // For simplicity, nothing to do. This is a stub to keep the receiver declared.
    }
}
