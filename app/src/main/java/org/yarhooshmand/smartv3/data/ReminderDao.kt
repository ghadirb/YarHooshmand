package org.yarhooshmand.smartv3.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders ORDER BY CASE WHEN date IS NULL THEN 1 ELSE 0 END, date ASC")
    fun getAll(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    fun getById(id: Long): Flow<ReminderEntity?>

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    suspend fun getByIdOnce(id: Long): ReminderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(reminder: ReminderEntity): Long

    @Update
    suspend fun update(reminder: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Query("UPDATE reminders SET done = :done, completed = :done, completedAt = CASE WHEN :done THEN :ts ELSE NULL END WHERE id = :id")
    suspend fun markDone(id: Long, done: Boolean, ts: Long?)
}
