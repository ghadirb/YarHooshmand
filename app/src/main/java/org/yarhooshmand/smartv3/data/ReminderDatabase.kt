package org.yarhooshmand.smartv3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Reminder::class], version = 1, exportSchema = false)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile private var INSTANCE: ReminderDatabase? = null

        fun get(context: Context): ReminderDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ReminderDatabase::class.java,
                    "reminder_db"
                )
                // توسعه: اگر تغییر توی Entity بدی، دیتابیس پاک و دوباره ساخته میشه
                .fallbackToDestructiveMigration()
                .build()
                    .also { INSTANCE = it }
            }
    }
}
