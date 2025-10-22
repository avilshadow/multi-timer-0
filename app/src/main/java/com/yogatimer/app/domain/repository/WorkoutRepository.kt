package com.yogatimer.app.domain.repository

import com.yogatimer.app.domain.model.Workout
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {

    /**
     * Get all workouts as a Flow
     */
    fun getAllWorkouts(): Flow<List<Workout>>

    /**
     * Get a specific workout by ID
     */
    suspend fun getWorkoutById(id: Long): Workout?

    /**
     * Create a new workout
     * @return The ID of the newly created workout
     */
    suspend fun createWorkout(workout: Workout): Long

    /**
     * Update an existing workout
     */
    suspend fun updateWorkout(workout: Workout)

    /**
     * Delete a workout by ID
     */
    suspend fun deleteWorkout(workoutId: Long)
}
