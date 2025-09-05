package org.yarhooshmand.smartv3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val note: String? = null,
    val date: Long,                // زمان ثبت/یادآوری
    val done: Boolean = false,     // انجام شده یا نه
    val completed: Boolean = false,// وضعیت تکمیل
    val completedAt: Long? = null  // زمان تکمیل
)
