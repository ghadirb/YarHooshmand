package org.yarhooshmand.smartv3.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ReminderEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
}
