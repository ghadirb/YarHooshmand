package org.yarhooshmand.smartv3.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    // گرفتن همه ریمایندرها به صورت زنده (Flow)
    @Query("SELECT * FROM reminders ORDER BY timeMillis ASC")
    fun getAll(): Flow<List<ReminderEntity>>

    // گرفتن همه ریمایندرها به صورت یک‌باره (برای Backup)
    @Query("SELECT * FROM reminders ORDER BY timeMillis ASC")
    suspend fun getAllOnce(): List<ReminderEntity>

    // گرفتن یک ریمایندر خاص با ID
    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    fun getReminderById(id: Long): Flow<ReminderEntity?>

    // اضافه کردن ریمایندر
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity): Long

    // اضافه کردن چند ریمایندر (برای Restore)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reminders: List<ReminderEntity>)

    // آپدیت
    @Update
    suspend fun update(reminder: ReminderEntity)

    // حذف
    @Delete
    suspend fun delete(reminder: ReminderEntity)
}
