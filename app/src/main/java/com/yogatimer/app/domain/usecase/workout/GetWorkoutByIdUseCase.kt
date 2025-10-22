package com.yogatimer.app.domain.usecase.workout

import com.yogatimer.app.domain.model.Workout
import com.yogatimer.app.domain.repository.WorkoutRepository
import javax.inject.Inject

class GetWorkoutByIdUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(id: Long): Workout? {
        return repository.getWorkoutById(id)
    }
}
