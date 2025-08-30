
package org.yarhooshmand.smartv3.data

import androidx.room.*

@Dao
interface ReminderDao {
    @Insert suspend fun insert(e: ReminderEntity): Long
    @Update suspend fun update(e: ReminderEntity)
    @Delete suspend fun delete(e: ReminderEntity)
    @Query("SELECT * FROM reminders ORDER BY timeMillis ASC")
    suspend fun getAll(): List<ReminderEntity>
    @Query("UPDATE reminders SET done=1 WHERE id=:id")
    suspend fun setDone(id: Long)
}
