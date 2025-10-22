package com.yogatimer.app.data.local.database.dao

import androidx.room.*
import com.yogatimer.app.data.local.database.entities.TimerEntity

@Dao
interface TimerDao {

    @Query("SELECT * FROM timers WHERE sectionId = :sectionId ORDER BY sortOrder ASC")
    suspend fun getTimersForSection(sectionId: Long): List<TimerEntity>

    @Query("SELECT * FROM timers WHERE id = :id")
    suspend fun getTimerById(id: Long): TimerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimer(timer: TimerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimers(timers: List<TimerEntity>): List<Long>

    @Update
    suspend fun updateTimer(timer: TimerEntity)

    @Delete
    suspend fun deleteTimer(timer: TimerEntity)

    @Query("DELETE FROM timers WHERE id = :id")
    suspend fun deleteTimerById(id: Long)

    @Query("DELETE FROM timers WHERE sectionId = :sectionId")
    suspend fun deleteTimersForSection(sectionId: Long)
}
