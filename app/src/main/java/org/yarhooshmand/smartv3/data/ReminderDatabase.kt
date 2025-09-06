package org.yarhooshmand.smartv3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// مطمئن شو که ReminderEntity درست ایمپورت شده
@Database(
    entities = [ReminderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ReminderDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: ReminderDatabase? = null

        fun getInstance(context: Context): ReminderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReminderDatabase::class.java,
                    "reminder_database"
                )
                    .fallbackToDestructiveMigration() // اگر اسکیما عوض شد، دیتابیس رو ریست کن
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
