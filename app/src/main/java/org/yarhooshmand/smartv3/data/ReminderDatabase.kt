package org.yarhooshmand.smartv3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * نکته مهم:
 * - بعضی بخش‌های پروژه ممکن است از AppDatabase و برخی از ReminderDatabase.get(...) استفاده کنند.
 *   برای سازگاری کامل:
 *   1) کلاس @Database با نام AppDatabase تعریف شده.
 *   2) یک wrapper به نام ReminderDatabase قرار داده شده که همان get(context) را برمی‌گرداند.
 * - version = 2 و fallbackToDestructiveMigration() برای حل mismatch ستون‌ها (date/completed) در بیلد فعلی.
 */
@Database(entities = [Reminder::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // هر دو امضا را نگه می‌داریم تا با کدهای مختلف سازگار باشد
    abstract fun reminderDao(): ReminderDao
    abstract fun dao(): ReminderDao

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
 * Wrapper برای سازگاری با کدهای قدیمی که از ReminderDatabase.get(context) استفاده می‌کنند.
 */
object ReminderDatabase {
    fun get(context: Context): AppDatabase = AppDatabase.get(context)
}
