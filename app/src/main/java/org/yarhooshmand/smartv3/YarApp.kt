package org.yarhooshmand.smartv3

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import kotlinx.coroutines.*
import org.yarhooshmand.smartv3.keys.KeysManager
import org.yarhooshmand.smartv3.models.ModelManager

class YarApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(
                NotificationChannel("rem_channel", "Reminders", NotificationManager.IMPORTANCE_HIGH)
            )
            nm.createNotificationChannel(
                NotificationChannel("voice_listen", "Voice Listening", NotificationManager.IMPORTANCE_LOW)
            )
        }
        GlobalScope.launch(Dispatchers.IO) {
            KeysManager.init(this@YarApp)
            ModelManager.init(this@YarApp)
        }
    }
}
