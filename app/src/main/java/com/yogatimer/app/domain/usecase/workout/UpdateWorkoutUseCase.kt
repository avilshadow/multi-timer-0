package com.yogatimer.app.domain.usecase.workout

import com.yogatimer.app.domain.model.Workout
import com.yogatimer.app.domain.repository.WorkoutRepository
import javax.inject.Inject

class UpdateWorkoutUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(workout: Workout) {
        require(workout.name.isNotBlank()) { "Workout name cannot be blank" }
        require(workout.calculateTotalTimers() > 0) { "Workout must have at least one timer" }

        repository.updateWorkout(workout)
    }
}
