package org.yarhooshmand.smartv3.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

class ReminderRepository private constructor(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: ReminderRepository? = null

        fun getInstance(context: Context): ReminderRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ReminderRepository(context).also { INSTANCE = it }
            }
        }

        // ⚡ Migration از نسخه 1 به 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE reminders ADD COLUMN text TEXT")
                database.execSQL("ALTER TABLE reminders ADD COLUMN timeMillis INTEGER")
                database.execSQL("ALTER TABLE reminders ADD COLUMN category TEXT")
                database.execSQL("ALTER TABLE reminders ADD COLUMN smsTargets TEXT")
            }
        }
    }

    private val db: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "reminders.db"
    )
        .addMigrations(MIGRATION_1_2) // ⚡ اضافه شدن migration
        .build()

    private val reminderDao = db.reminderDao()

    fun getAllReminders(): Flow<List<ReminderEntity>> = reminderDao.getAll()

    suspend fun insert(reminder: ReminderEntity): Long = reminderDao.insert(reminder)

    suspend fun update(reminder: ReminderEntity) = reminderDao.update(reminder)

    suspend fun delete(reminder: ReminderEntity) = reminderDao.delete(reminder)

    fun getReminderById(id: Long): Flow<ReminderEntity> = reminderDao.getReminderById(id)
}
