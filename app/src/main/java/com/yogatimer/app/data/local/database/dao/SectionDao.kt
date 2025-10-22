package com.yogatimer.app.data.local.database.dao

import androidx.room.*
import com.yogatimer.app.data.local.database.entities.SectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionDao {

    @Query("SELECT * FROM sections WHERE workoutId = :workoutId ORDER BY sortOrder ASC")
    fun getSectionsForWorkout(workoutId: Long): Flow<List<SectionEntity>>

    @Query("SELECT * FROM sections WHERE workoutId = :workoutId ORDER BY sortOrder ASC")
    suspend fun getSectionsForWorkoutSync(workoutId: Long): List<SectionEntity>

    @Query("SELECT * FROM sections WHERE parentSectionId = :parentId ORDER BY sortOrder ASC")
    suspend fun getChildSections(parentId: Long): List<SectionEntity>

    @Query("SELECT * FROM sections WHERE id = :id")
    suspend fun getSectionById(id: Long): SectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(section: SectionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSections(sections: List<SectionEntity>): List<Long>

    @Update
    suspend fun updateSection(section: SectionEntity)

    @Delete
    suspend fun deleteSection(section: SectionEntity)

    @Query("DELETE FROM sections WHERE workoutId = :workoutId")
    suspend fun deleteSectionsForWorkout(workoutId: Long)

    @Query("DELETE FROM sections WHERE id = :id")
    suspend fun deleteSectionById(id: Long)
}
