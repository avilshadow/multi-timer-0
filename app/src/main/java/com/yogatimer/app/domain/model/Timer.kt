package com.yogatimer.app.domain.model

data class Timer(
    val id: Long = 0,
    val sectionId: Long,
    val name: String,
    val description: String = "",
    val durationSeconds: Int
) {
    /**
     * Get formatted duration as MM:SS
     */
    fun getFormattedDuration(): String {
        val mins = durationSeconds / 60
        val secs = durationSeconds % 60
        return "%d:%02d".format(mins, secs)
    }

    /**
     * Validate timer has valid duration
     */
    fun isValid(): Boolean {
        return name.isNotBlank() && durationSeconds > 0
    }
}
