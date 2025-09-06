package org.yarhooshmand.smartv3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Reminder::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // فقط یک متد برای دسترسی مستقیم به DAO
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
 * Wrapper برای سازگاری با کدهای قدیمی.
 * اگر جایی در پروژه نوشته بود: ReminderDatabase.get(context).dao()
 * اینجا map می‌کنیم به AppDatabase.get(context).reminderDao()
 */
object ReminderDatabase {
    fun get(context: Context): AppDatabase = AppDatabase.get(context)

    fun dao(context: Context): ReminderDao = get(context).reminderDao()
}
