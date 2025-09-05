package org.yarhooshmand.smartv3.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders ORDER BY date ASC")
    fun getAll(): Flow<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder)

    @Update
    suspend fun update(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    @Query("DELETE FROM reminders")
    suspend fun clearAll()

    @Query("UPDATE reminders SET completed = 1, completedAt = :ts WHERE id = :id")
    suspend fun markCompleted(id: Int, ts: Long)
}
