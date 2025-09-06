package org.yarhooshmand.smartv3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ReminderEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(SmsTargetsConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "reminders_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private val MIGRATION_1_2 = androidx.room.migration.Migration(1, 2) {
            it.execSQL("ALTER TABLE reminders ADD COLUMN category TEXT")
            it.execSQL("ALTER TABLE reminders ADD COLUMN smsTargets TEXT")
            it.execSQL("ALTER TABLE reminders ADD COLUMN completed INTEGER NOT NULL DEFAULT 0")
            it.execSQL("ALTER TABLE reminders ADD COLUMN completedAt INTEGER")
        }
    }
}
