package com.yogatimer.app.domain.model

data class Section(
    val id: Long = 0,
    val workoutId: Long,
    val parentSectionId: Long? = null,
    val name: String,
    val description: String = "",
    val repeatCount: Int = 1,
    val timers: List<Timer> = emptyList(),
    val childSections: List<Section> = emptyList(),
    val level: Int = 0
) {
    /**
     * Calculate total number of timers including nested sections and repeats
     */
    fun calculateTotalTimers(): Int {
        val timersInThisSection = timers.size
        val timersInChildSections = childSections.sumOf { it.calculateTotalTimers() }
        val totalTimersPerRepeat = timersInThisSection + timersInChildSections
        return totalTimersPerRepeat * repeatCount
    }

    /**
     * Calculate total duration in seconds including nested sections and repeats
     */
    fun calculateTotalDuration(): Int {
        val durationInThisSection = timers.sumOf { it.durationSeconds }
        val durationInChildSections = childSections.sumOf { it.calculateTotalDuration() }
        val totalDurationPerRepeat = durationInThisSection + durationInChildSections
        return totalDurationPerRepeat * repeatCount
    }

    /**
     * Get timers in one iteration (without accounting for repeats)
     */
    fun getTimersInOneIteration(): List<Timer> {
        val result = mutableListOf<Timer>()
        result.addAll(timers)
        childSections.forEach { child ->
            result.addAll(child.getTimersInOneIteration())
        }
        return result
    }

    /**
     * Get flattened timers accounting for all repeats
     */
    fun getFlattenedTimers(startGlobalIndex: Int): List<FlattenedTimer> {
        val result = mutableListOf<FlattenedTimer>()
        val timersPerRepeat = getTimersInOneIteration()
        val totalTimersInRepeat = timersPerRepeat.size
        var globalIndex = startGlobalIndex

        repeat(repeatCount) { repeatIndex ->
            timersPerRepeat.forEachIndexed { timerIndex, timer ->
                result.add(
                    FlattenedTimer(
                        timer = timer,
                        section = this,
                        sectionRepeat = repeatIndex + 1,
                        totalSectionRepeats = repeatCount,
                        timerIndexInRepeat = timerIndex,
                        totalTimersInRepeat = totalTimersInRepeat,
                        globalIndex = globalIndex
                    )
                )
                globalIndex++
            }
        }

        return result
    }

    /**
     * Check if this section has nested child sections
     */
    fun hasChildSections(): Boolean = childSections.isNotEmpty()

    /**
     * Check if this section has multiple repeats
     */
    fun hasRepeats(): Boolean = repeatCount > 1
}
