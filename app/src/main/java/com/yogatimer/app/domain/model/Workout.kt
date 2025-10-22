package com.yogatimer.app.domain.model

data class Workout(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val sections: List<Section> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPreloaded: Boolean = false
) {
    /**
     * Calculate total number of timers (accounting for repeats)
     */
    fun calculateTotalTimers(): Int {
        return sections.sumOf { it.calculateTotalTimers() }
    }

    /**
     * Calculate total duration in seconds (accounting for repeats)
     */
    fun calculateTotalDuration(): Int {
        return sections.sumOf { it.calculateTotalDuration() }
    }

    /**
     * Get all timers in a flattened list (accounting for section repeats)
     */
    fun getAllTimersFlattened(): List<FlattenedTimer> {
        val result = mutableListOf<FlattenedTimer>()
        var globalIndex = 0

        sections.forEach { section ->
            result.addAll(section.getFlattenedTimers(globalIndex))
            globalIndex += section.calculateTotalTimers()
        }

        return result
    }
}

/**
 * Represents a timer with its section context after flattening repeats
 */
data class FlattenedTimer(
    val timer: Timer,
    val section: Section,
    val sectionRepeat: Int,         // Current repeat of the section (1-based)
    val totalSectionRepeats: Int,   // Total number of repeats for this section
    val timerIndexInRepeat: Int,    // Index of this timer within the current repeat (0-based)
    val totalTimersInRepeat: Int,   // Total timers in one repeat of the section
    val globalIndex: Int            // Overall index in the workout (0-based)
)
