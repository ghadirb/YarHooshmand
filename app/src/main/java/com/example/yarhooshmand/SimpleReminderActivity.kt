package com.example.yarhooshmand

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yarhooshmand.databinding.ActivitySimpleBinding

class SimpleReminderActivity : AppCompatActivity() {
    private lateinit var b: ActivitySimpleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySimpleBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnSet.setOnClickListener {
            val msg = b.etMessage.text.toString().trim()
            if (msg.isEmpty()) { Toast.makeText(this,"متن وارد کنید",Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            val trigger = System.currentTimeMillis() + 60_000L // demo: 1 دقیقه بعد
            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(this, ReminderReceiver::class.java).putExtra("message", msg)
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_UPDATE_CURRENT
            val p = PendingIntent.getBroadcast(this, msg.hashCode(), intent, flags)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, p)
            else am.setExact(AlarmManager.RTC_WAKEUP, trigger, p)
            Toast.makeText(this,"یادآور تنظیم شد", Toast.LENGTH_SHORT).show()
        }
    }
}
