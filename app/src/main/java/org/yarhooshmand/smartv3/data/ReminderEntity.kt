package org.yarhooshmand.smartv3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val note: String? = null,
    /** epoch millis for due date; nullable if no date */
    val date: Long? = null,
    /** legacy done flag */
    val done: Boolean = false,
    /** completed == done for compatibility */
    val completed: Boolean = false,
    val completedAt: Long? = null
)
