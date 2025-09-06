package org.yarhooshmand.smartv3.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "reminders")
@TypeConverters(SmsTargetsConverter::class)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val timeMillis: Long,
    val category: String? = null,
    val smsTargets: List<String> = emptyList(),
    val done: Boolean = false,
    val completed: Boolean = false,
    val completedAt: Long? = null
)
