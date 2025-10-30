package com.yogatimer.app.presentation.timer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogatimer.app.domain.model.TimerState
import com.yogatimer.app.domain.model.Workout
import com.yogatimer.app.domain.timer.TimerManager
import com.yogatimer.app.domain.usecase.workout.GetWorkoutByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Active Timer Screen.
 *
 * Manages timer execution using TimerManager and exposes state to UI.
 */
@HiltViewModel
class ActiveTimerViewModel @Inject constructor(
    private val timerManager: TimerManager,
    private val getWorkoutByIdUseCase: GetWorkoutByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workoutId: Long = savedStateHandle.get<Long>("workoutId") ?: 0

    private val _uiState = MutableStateFlow(ActiveTimerUiState())
    val uiState: StateFlow<ActiveTimerUiState> = _uiState.asStateFlow()

    // Expose timer state from TimerManager
    val timerState: StateFlow<TimerState> = timerManager.state

    init {
        loadAndStartWorkout()
        observeTimerState()
    }

    /**
     * Load workout from database and start timer.
     */
    private fun loadAndStartWorkout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val workout = getWorkoutByIdUseCase(workoutId)
                if (workout != null) {
                    _uiState.update {
                        it.copy(
                            workout = workout,
                            isLoading = false
                        )
                    }
                    // Start the workout
                    timerManager.startWorkout(workout, viewModelScope)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Workout not found"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load workout"
                    )
                }
            }
        }
    }

    /**
     * Observe timer state changes.
     */
    private fun observeTimerState() {
        viewModelScope.launch {
            timerManager.state.collect { state ->
                when (state) {
                    is TimerState.Completed -> {
                        _uiState.update { it.copy(shouldNavigateBack = true) }
                    }
                    else -> {
                        // State is already exposed via timerState StateFlow
                    }
                }
            }
        }
    }

    /**
     * Toggle between pause and resume.
     */
    fun onPauseResume() {
        when (timerManager.state.value) {
            is TimerState.Running -> timerManager.pause()
            is TimerState.Paused -> timerManager.resume()
            else -> {
                // No-op for Idle or Completed states
            }
        }
    }

    /**
     * Skip to next timer.
     */
    fun onSkip() {
        timerManager.skip()
    }

    /**
     * Show stop confirmation dialog.
     */
    fun onStopRequest() {
        _uiState.update { it.copy(showStopConfirmation = true) }
    }

    /**
     * Confirm stop - stop timer and navigate back.
     */
    fun confirmStop() {
        timerManager.stop()
        _uiState.update {
            it.copy(
                showStopConfirmation = false,
                shouldNavigateBack = true
            )
        }
    }

    /**
     * Cancel stop request.
     */
    fun cancelStop() {
        _uiState.update { it.copy(showStopConfirmation = false) }
    }

    /**
     * Clear navigation flag after navigation is handled.
     */
    fun onNavigationHandled() {
        _uiState.update { it.copy(shouldNavigateBack = false) }
    }

    /**
     * Dismiss error message.
     */
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        // Stop timer when ViewModel is cleared (user navigates away)
        if (timerManager.state.value.isActive()) {
            timerManager.stop()
        }
    }
}

/**
 * UI State for Active Timer Screen.
 */
data class ActiveTimerUiState(
    val workout: Workout? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showStopConfirmation: Boolean = false,
    val shouldNavigateBack: Boolean = false
)
