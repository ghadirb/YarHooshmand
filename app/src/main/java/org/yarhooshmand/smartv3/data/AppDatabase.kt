package org.yarhooshmand.smartv3.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ReminderEntity::class], version = 2, exportSchema = false)
@TypeConverters(SmsTargetsConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "reminders.db"
                ).addMigrations(MIGRATION_1_2).build().also { INSTANCE = it }
            }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try { db.execSQL("ALTER TABLE reminders ADD COLUMN text TEXT NOT NULL DEFAULT ''") } catch (_: Exception) {}
                try { db.execSQL("ALTER TABLE reminders ADD COLUMN timeMillis INTEGER NOT NULL DEFAULT 0") } catch (_: Exception) {}
                try { db.execSQL("ALTER TABLE reminders ADD COLUMN category TEXT") } catch (_: Exception) {}
                try { db.execSQL("ALTER TABLE reminders ADD COLUMN smsTargets TEXT") } catch (_: Exception) {}
                try { db.execSQL("ALTER TABLE reminders ADD COLUMN done INTEGER NOT NULL DEFAULT 0") } catch (_: Exception) {}
                try { db.execSQL("ALTER TABLE reminders ADD COLUMN completed INTEGER NOT NULL DEFAULT 0") } catch (_: Exception) {}
                try { db.execSQL("ALTER TABLE reminders ADD COLUMN completedAt INTEGER") } catch (_: Exception) {}
            }
        }
    }
}
