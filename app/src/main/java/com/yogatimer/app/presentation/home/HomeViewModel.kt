package com.yogatimer.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogatimer.app.domain.model.Workout
import com.yogatimer.app.domain.usecase.workout.DeleteWorkoutUseCase
import com.yogatimer.app.domain.usecase.workout.GetAllWorkoutsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Home Screen.
 *
 * Manages the list of workouts and handles user actions like
 * creating, editing, and deleting workouts.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllWorkoutsUseCase: GetAllWorkoutsUseCase,
    private val deleteWorkoutUseCase: DeleteWorkoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadWorkouts()
    }

    /**
     * Load all workouts from database.
     */
    private fun loadWorkouts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getAllWorkoutsUseCase()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load workouts"
                        )
                    }
                }
                .collect { workouts ->
                    _uiState.update {
                        it.copy(
                            workouts = workouts,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    /**
     * Handle workout card click - navigate to active timer.
     */
    fun onWorkoutClick(workout: Workout) {
        _uiState.update { it.copy(selectedWorkout = workout) }
    }

    /**
     * Handle workout long click or more options - show menu.
     */
    fun onWorkoutMoreClick(workout: Workout) {
        _uiState.update { it.copy(workoutMenuTarget = workout) }
    }

    /**
     * Dismiss workout context menu.
     */
    fun dismissWorkoutMenu() {
        _uiState.update { it.copy(workoutMenuTarget = null) }
    }

    /**
     * Navigate to edit workout screen.
     */
    fun onEditWorkout(workout: Workout) {
        _uiState.update {
            it.copy(
                workoutToEdit = workout,
                workoutMenuTarget = null
            )
        }
    }

    /**
     * Show delete confirmation dialog.
     */
    fun onDeleteWorkoutRequest(workout: Workout) {
        _uiState.update {
            it.copy(
                workoutToDelete = workout,
                workoutMenuTarget = null
            )
        }
    }

    /**
     * Confirm and execute workout deletion.
     */
    fun confirmDeleteWorkout() {
        val workoutToDelete = _uiState.value.workoutToDelete ?: return

        viewModelScope.launch {
            try {
                deleteWorkoutUseCase(workoutToDelete.id)
                _uiState.update { it.copy(workoutToDelete = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Failed to delete workout",
                        workoutToDelete = null
                    )
                }
            }
        }
    }

    /**
     * Cancel workout deletion.
     */
    fun cancelDeleteWorkout() {
        _uiState.update { it.copy(workoutToDelete = null) }
    }

    /**
     * Navigate to create workout screen.
     */
    fun onCreateWorkout() {
        _uiState.update { it.copy(shouldNavigateToCreate = true) }
    }

    /**
     * Clear navigation flags after navigation is handled.
     */
    fun onNavigationHandled() {
        _uiState.update {
            it.copy(
                selectedWorkout = null,
                workoutToEdit = null,
                shouldNavigateToCreate = false
            )
        }
    }

    /**
     * Dismiss error message.
     */
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * UI State for Home Screen.
 */
data class HomeUiState(
    val workouts: List<Workout> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedWorkout: Workout? = null,
    val workoutMenuTarget: Workout? = null,
    val workoutToEdit: Workout? = null,
    val workoutToDelete: Workout? = null,
    val shouldNavigateToCreate: Boolean = false
)
