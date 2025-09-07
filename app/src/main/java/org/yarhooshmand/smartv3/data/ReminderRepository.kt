package org.yarhooshmand.smartv3.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class ReminderRepository private constructor(
    private val dao: ReminderDao
) {
    val allReminders: Flow<List<ReminderEntity>> = dao.getAll()

    fun getById(id: Long): Flow<ReminderEntity?> = dao.getById(id)

    suspend fun getByIdOnce(id: Long): ReminderEntity? = dao.getByIdOnce(id)

    suspend fun add(title: String, note: String? = null, date: Long? = null): Long {
        val entity = ReminderEntity(title = title, note = note, date = date)
        return dao.upsert(entity)
    }

    suspend fun update(entity: ReminderEntity) {
        dao.upsert(entity)
    }

    suspend fun toggleDone(id: Long) {
        val current = dao.getByIdOnce(id) ?: return
        val newDone = !current.done
        dao.markDone(id, newDone, if (newDone) System.currentTimeMillis() else null)
    }

    suspend fun delete(id: Long) {
        val current = dao.getByIdOnce(id) ?: return
        dao.delete(current)
    }

    companion object {
        @Volatile private var INSTANCE: ReminderRepository? = null
        fun get(context: Context): ReminderRepository =
            INSTANCE ?: synchronized(this) {
                val db = ReminderDatabase.get(context)
                val repo = ReminderRepository(db.reminderDao())
                INSTANCE = repo
                repo
            }
    }
}
