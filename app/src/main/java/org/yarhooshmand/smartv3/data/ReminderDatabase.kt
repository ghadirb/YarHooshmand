package org.yarhooshmand.smartv3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * دیتابیس اصلی برنامه
 * فقط یک متد DAO تعریف شده (reminderDao) تا تداخلی پیش نیاد
 */
@Database(entities = [Reminder::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "reminder_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                    .also { INSTANCE = it }
            }
    }
}

/**
 * Wrapper برای سازگاری با کدهای قدیمی که از ReminderDatabase.get(context) استفاده می‌کنند
 */
object ReminderDatabase {
    fun get(context: Context): AppDatabase = AppDatabase.get(context)
}
