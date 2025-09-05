package org.yarhooshmand.smartv3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val note: String? = null,
    val date: Long,                // زمان یادآوری به صورت epoch millis
    val done: Boolean = false,     // وضعیت انجام (در صورت نیاز)
    val completed: Boolean = false,// برای تیک «انجام شد»
    val completedAt: Long? = null  // زمان ثبت انجام
)
