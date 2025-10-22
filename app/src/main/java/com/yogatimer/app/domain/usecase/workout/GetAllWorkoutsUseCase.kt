package com.yogatimer.app.domain.usecase.workout

import com.yogatimer.app.domain.model.Workout
import com.yogatimer.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllWorkoutsUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    operator fun invoke(): Flow<List<Workout>> {
        return repository.getAllWorkouts()
    }
}
