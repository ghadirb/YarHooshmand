package org.yarhooshmand.smartv3.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "note") val note: String? = null,
    @ColumnInfo(name = "date") val date: Long? = null,

    @ColumnInfo(name = "done") val done: Boolean = false,
    @ColumnInfo(name = "completed") val completed: Boolean = false,
    @ColumnInfo(name = "completedAt") val completedAt: Long? = null,

    // امکانات اضافه
    @ColumnInfo(name = "text") val text: String? = null,
    @ColumnInfo(name = "timeMillis") val timeMillis: Long? = null,
    @ColumnInfo(name = "category") val category: String? = null,
    @ColumnInfo(name = "smsTargets") val smsTargets: String? = null
)
