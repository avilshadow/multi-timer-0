package com.yogatimer.app.domain.model

/**
 * Represents the state of the timer execution
 */
sealed class TimerState {
    object Idle : TimerState()

    data class Running(
        val currentSection: Section,
        val currentTimer: Timer,
        val remainingSeconds: Int,
        val sectionProgress: SectionProgress,
        val overallProgress: WorkoutProgress
    ) : TimerState()

    data class Paused(
        val currentSection: Section,
        val currentTimer: Timer,
        val remainingSeconds: Int,
        val sectionProgress: SectionProgress,
        val overallProgress: WorkoutProgress
    ) : TimerState()

    object Completed : TimerState()

    /**
     * Check if timer is actively running
     */
    fun isRunning(): Boolean = this is Running

    /**
     * Check if timer is paused
     */
    fun isPaused(): Boolean = this is Paused

    /**
     * Check if timer is active (running or paused)
     */
    fun isActive(): Boolean = this is Running || this is Paused

    /**
     * Get current timer if active, null otherwise
     */
    fun getTimerOrNull(): Timer? {
        return when (this) {
            is Running -> currentTimer
            is Paused -> currentTimer
            else -> null
        }
    }

    /**
     * Get remaining seconds if active, null otherwise
     */
    fun getSecondsRemaining(): Int? {
        return when (this) {
            is Running -> remainingSeconds
            is Paused -> remainingSeconds
            else -> null
        }
    }
}
