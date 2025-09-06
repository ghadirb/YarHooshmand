package org.yarhooshmand.smartv3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val text: String,
    val timeMillis: Long,
    val category: String = "عمومی",
    val smsTargets: String? = null,
    val done: Boolean = false
)
