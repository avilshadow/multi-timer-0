package com.yogatimer.app.domain.usecase.workout

import com.yogatimer.app.domain.repository.WorkoutRepository
import javax.inject.Inject

class DeleteWorkoutUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(workoutId: Long) {
        repository.deleteWorkout(workoutId)
    }
}
