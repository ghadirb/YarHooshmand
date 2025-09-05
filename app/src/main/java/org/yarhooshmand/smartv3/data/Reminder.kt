package org.yarhooshmand.smartv3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val note: String? = null,
    val date: Long,             // epoch millis
    val done: Boolean = false,
    val completed: Boolean = false,
    val completedAt: Long? = null
)
