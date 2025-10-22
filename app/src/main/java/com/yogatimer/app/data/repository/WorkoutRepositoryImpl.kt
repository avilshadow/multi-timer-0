package com.yogatimer.app.data.repository

import com.yogatimer.app.data.local.database.dao.SectionDao
import com.yogatimer.app.data.local.database.dao.TimerDao
import com.yogatimer.app.data.local.database.dao.WorkoutDao
import com.yogatimer.app.data.local.database.entities.SectionEntity
import com.yogatimer.app.data.mapper.toDomainModel
import com.yogatimer.app.data.mapper.toEntity
import com.yogatimer.app.domain.model.Section
import com.yogatimer.app.domain.model.Workout
import com.yogatimer.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val sectionDao: SectionDao,
    private val timerDao: TimerDao
) : WorkoutRepository {

    override fun getAllWorkouts(): Flow<List<Workout>> {
        return workoutDao.getAllWorkouts().map { entities ->
            entities.map { entity ->
                val sections = loadSectionsForWorkout(entity.id)
                entity.toDomainModel(sections)
            }
        }
    }

    override suspend fun getWorkoutById(id: Long): Workout? {
        val entity = workoutDao.getWorkoutById(id) ?: return null
        val sections = loadSectionsForWorkout(id)
        return entity.toDomainModel(sections)
    }

    override suspend fun createWorkout(workout: Workout): Long {
        // 1. Insert workout
        val workoutId = workoutDao.insertWorkout(workout.toEntity())

        // 2. Insert sections and timers recursively
        workout.sections.forEachIndexed { index, section ->
            insertSectionRecursively(
                section = section.copy(workoutId = workoutId),
                parentId = null,
                sortOrder = index
            )
        }

        return workoutId
    }

    override suspend fun updateWorkout(workout: Workout) {
        // Update workout entity
        workoutDao.updateWorkout(workout.toEntity())

        // Delete existing sections and timers (cascade will handle timers)
        sectionDao.deleteSectionsForWorkout(workout.id)

        // Recreate sections and timers
        workout.sections.forEachIndexed { index, section ->
            insertSectionRecursively(
                section = section.copy(workoutId = workout.id),
                parentId = null,
                sortOrder = index
            )
        }
    }

    override suspend fun deleteWorkout(workoutId: Long) {
        workoutDao.deleteWorkoutById(workoutId)
    }

    /**
     * Recursively insert a section and its children/timers
     */
    private suspend fun insertSectionRecursively(
        section: Section,
        parentId: Long?,
        sortOrder: Int
    ) {
        // Insert section
        val sectionEntity = section.toEntity().copy(
            parentSectionId = parentId,
            sortOrder = sortOrder
        )
        val sectionId = sectionDao.insertSection(sectionEntity)

        // Insert timers for this section
        section.timers.forEachIndexed { index, timer ->
            timerDao.insertTimer(
                timer.toEntity().copy(
                    sectionId = sectionId,
                    sortOrder = index
                )
            )
        }

        // Recursively insert child sections
        section.childSections.forEachIndexed { index, childSection ->
            insertSectionRecursively(
                section = childSection.copy(
                    workoutId = section.workoutId,
                    level = section.level + 1
                ),
                parentId = sectionId,
                sortOrder = index
            )
        }
    }

    /**
     * Load all sections for a workout (root sections only)
     */
    private suspend fun loadSectionsForWorkout(workoutId: Long): List<Section> {
        return sectionDao.getSectionsForWorkout(workoutId)
            .first()
            .filter { it.parentSectionId == null }
            .map { loadSectionRecursively(it) }
    }

    /**
     * Recursively load a section with its timers and child sections
     */
    private suspend fun loadSectionRecursively(entity: SectionEntity): Section {
        // Load timers for this section
        val timers = timerDao.getTimersForSection(entity.id)
            .map { it.toDomainModel() }

        // Load child sections recursively
        val childSections = sectionDao.getChildSections(entity.id)
            .map { loadSectionRecursively(it) }

        return entity.toDomainModel(timers, childSections)
    }
}
