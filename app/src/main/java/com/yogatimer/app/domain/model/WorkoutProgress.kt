package com.yogatimer.app.domain.model

/**
 * Tracks overall workout progress
 */
data class WorkoutProgress(
    val currentSectionIndex: Int,       // Flattened section index
    val totalSections: Int,             // Total sections after expansion
    val currentTimerGlobal: Int,        // Global timer number (1-based for display)
    val totalTimersGlobal: Int,         // Total timers in workout
    val elapsedSeconds: Int,            // Total elapsed time
    val totalSeconds: Int               // Total workout duration
) {
    /**
     * Calculate overall progress as percentage
     */
    fun getProgressPercentage(): Float {
        if (totalTimersGlobal == 0) return 0f
        return currentTimerGlobal.toFloat() / totalTimersGlobal.toFloat()
    }

    /**
     * Calculate time progress as percentage
     */
    fun getTimeProgressPercentage(): Float {
        if (totalSeconds == 0) return 0f
        return elapsedSeconds.toFloat() / totalSeconds.toFloat()
    }
}

/**
 * Tracks progress within a section (especially for sections with repeats)
 */
data class SectionProgress(
    val sectionId: Long,
    val currentRepeat: Int,         // 1-based
    val totalRepeats: Int,
    val currentTimerIndex: Int,     // 0-based index within repeat
    val totalTimers: Int            // Total timers in one repeat
) {
    /**
     * Calculate total repeat progress (completed repeats)
     */
    fun getTotalRepeatProgress(): Float {
        if (totalRepeats <= 1) return 1f
        return (currentRepeat - 1).toFloat() / totalRepeats.toFloat()
    }

    /**
     * Calculate current repeat progress
     */
    fun getCurrentRepeatProgress(): Float {
        if (totalTimers == 0) return 0f
        return currentTimerIndex.toFloat() / totalTimers.toFloat()
    }

    /**
     * Calculate combined progress (total + current repeat increment)
     */
    fun getCombinedProgress(): Float {
        if (totalRepeats <= 1) return getCurrentRepeatProgress()

        val totalProgress = getTotalRepeatProgress()
        val currentProgress = getCurrentRepeatProgress()
        val incrementProgress = currentProgress / totalRepeats.toFloat()
        return totalProgress + incrementProgress
    }
}
