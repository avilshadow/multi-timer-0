package com.yogatimer.app.domain.timer

import com.yogatimer.app.domain.model.FlattenedTimer
import com.yogatimer.app.domain.model.SectionProgress
import com.yogatimer.app.domain.model.TimerState
import com.yogatimer.app.domain.model.Workout
import com.yogatimer.app.domain.model.WorkoutProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TimerManager handles the execution of workout timers.
 *
 * Features:
 * - Flattens nested sections with repeats into linear sequence
 * - Countdown state machine (Idle → Running → Paused → Completed)
 * - Play/pause/skip/stop controls
 * - Progress tracking for sections and overall workout
 * - State updates via Flow for reactive UI
 *
 * Usage:
 * ```
 * timerManager.startWorkout(workout)
 * timerManager.state.collect { state ->
 *     // React to state changes
 * }
 * timerManager.pause()
 * timerManager.resume()
 * timerManager.skip()
 * timerManager.stop()
 * ```
 */
@Singleton
class TimerManager @Inject constructor() {

    private val _state = MutableStateFlow<TimerState>(TimerState.Idle)
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private var currentWorkout: Workout? = null
    private var flattenedTimers: List<FlattenedTimer> = emptyList()
    private var currentTimerIndex: Int = 0
    private var remainingSeconds: Int = 0
    private var totalElapsedSeconds: Int = 0
    private var countdownJob: Job? = null
    private var timerScope: CoroutineScope? = null

    /**
     * Start a workout from the beginning.
     *
     * @param workout The workout to execute
     * @param scope CoroutineScope for countdown coroutines
     */
    fun startWorkout(workout: Workout, scope: CoroutineScope) {
        // Stop any existing workout
        stop()

        // Set up new workout
        currentWorkout = workout
        timerScope = scope
        flattenedTimers = workout.getAllTimersFlattened()
        currentTimerIndex = 0
        totalElapsedSeconds = 0

        if (flattenedTimers.isEmpty()) {
            _state.value = TimerState.Completed
            return
        }

        // Start first timer
        startCurrentTimer()
    }

    /**
     * Resume from paused state.
     */
    fun resume() {
        val currentState = _state.value
        if (currentState is TimerState.Paused) {
            _state.value = TimerState.Running(
                currentSection = currentState.currentSection,
                currentTimer = currentState.currentTimer,
                remainingSeconds = currentState.remainingSeconds,
                sectionProgress = currentState.sectionProgress,
                overallProgress = currentState.overallProgress
            )
            startCountdown()
        }
    }

    /**
     * Pause the current timer.
     */
    fun pause() {
        val currentState = _state.value
        if (currentState is TimerState.Running) {
            stopCountdown()
            _state.value = TimerState.Paused(
                currentSection = currentState.currentSection,
                currentTimer = currentState.currentTimer,
                remainingSeconds = currentState.remainingSeconds,
                sectionProgress = currentState.sectionProgress,
                overallProgress = currentState.overallProgress
            )
        }
    }

    /**
     * Skip to the next timer.
     */
    fun skip() {
        if (_state.value.isActive()) {
            stopCountdown()
            moveToNextTimer()
        }
    }

    /**
     * Stop the workout completely.
     */
    fun stop() {
        stopCountdown()
        _state.value = TimerState.Idle
        currentWorkout = null
        flattenedTimers = emptyList()
        currentTimerIndex = 0
        remainingSeconds = 0
        totalElapsedSeconds = 0
        timerScope = null
    }

    /**
     * Get current workout.
     */
    fun getCurrentWorkout(): Workout? = currentWorkout

    // Private helper functions

    private fun startCurrentTimer() {
        if (currentTimerIndex >= flattenedTimers.size) {
            // Workout completed
            _state.value = TimerState.Completed
            return
        }

        val flattenedTimer = flattenedTimers[currentTimerIndex]
        remainingSeconds = flattenedTimer.timer.durationSeconds

        // Build progress objects
        val sectionProgress = SectionProgress(
            sectionId = flattenedTimer.section.id,
            currentRepeat = flattenedTimer.sectionRepeat,
            totalRepeats = flattenedTimer.totalSectionRepeats,
            currentTimerIndex = flattenedTimer.timerIndexInRepeat,
            totalTimers = flattenedTimer.totalTimersInRepeat
        )

        val overallProgress = WorkoutProgress(
            currentSectionIndex = 0, // Could be improved to track section index
            totalSections = currentWorkout?.sections?.size ?: 0,
            currentTimerGlobal = currentTimerIndex + 1, // 1-based for display
            totalTimersGlobal = flattenedTimers.size,
            elapsedSeconds = totalElapsedSeconds,
            totalSeconds = currentWorkout?.calculateTotalDuration() ?: 0
        )

        // Update state to Running
        _state.value = TimerState.Running(
            currentSection = flattenedTimer.section,
            currentTimer = flattenedTimer.timer,
            remainingSeconds = remainingSeconds,
            sectionProgress = sectionProgress,
            overallProgress = overallProgress
        )

        // Start countdown
        startCountdown()
    }

    private fun startCountdown() {
        stopCountdown() // Ensure no existing countdown

        countdownJob = timerScope?.launch {
            while (remainingSeconds > 0) {
                delay(1000) // 1 second tick

                remainingSeconds--
                totalElapsedSeconds++

                // Update state with new remaining seconds
                val currentState = _state.value
                if (currentState is TimerState.Running) {
                    _state.value = currentState.copy(
                        remainingSeconds = remainingSeconds,
                        overallProgress = currentState.overallProgress.copy(
                            elapsedSeconds = totalElapsedSeconds
                        )
                    )
                }
            }

            // Timer completed, move to next
            moveToNextTimer()
        }
    }

    private fun stopCountdown() {
        countdownJob?.cancel()
        countdownJob = null
    }

    private fun moveToNextTimer() {
        currentTimerIndex++

        if (currentTimerIndex >= flattenedTimers.size) {
            // All timers completed
            _state.value = TimerState.Completed
        } else {
            // Start next timer
            startCurrentTimer()
        }
    }
}
