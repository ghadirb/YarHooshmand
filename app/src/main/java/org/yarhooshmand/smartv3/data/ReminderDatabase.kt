package org.yarhooshmand.smartv3.data

import android.content.Context
import androidx.room.Room

object ReminderDatabase {
    @Volatile private var INSTANCE: AppDatabase? = null

    fun get(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "reminders.db"
            )
            // NOTE: In production, add proper Migration(s) instead of fallback
            .fallbackToDestructiveMigration()
            .build()
            INSTANCE = db
            db
        }
    }
}
