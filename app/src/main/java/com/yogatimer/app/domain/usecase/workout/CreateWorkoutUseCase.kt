package com.yogatimer.app.domain.usecase.workout

import com.yogatimer.app.domain.model.Workout
import com.yogatimer.app.domain.repository.WorkoutRepository
import javax.inject.Inject

class CreateWorkoutUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(workout: Workout): Long {
        require(workout.name.isNotBlank()) { "Workout name cannot be blank" }
        require(workout.calculateTotalTimers() > 0) { "Workout must have at least one timer" }

        return repository.createWorkout(workout)
    }
}
