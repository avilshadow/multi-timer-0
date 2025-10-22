package com.yogatimer.app.util

object TimeFormatter {

    /**
     * Formats seconds to MM:SS format
     * @param seconds Total seconds
     * @return Formatted string like "1:45" or "12:03"
     */
    fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "%d:%02d".format(mins, secs)
    }

    /**
     * Formats seconds to human-readable duration
     * @param seconds Total seconds
     * @return Formatted string like "5 min" or "1h 30min"
     */
    fun formatDuration(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60

        return when {
            hours > 0 -> "${hours}h ${minutes}min"
            minutes > 0 -> "${minutes} min"
            else -> "${seconds}s"
        }
    }

    /**
     * Parses MM:SS format to total seconds
     * @param timeString Time in "MM:SS" format
     * @return Total seconds
     */
    fun parseTime(timeString: String): Int? {
        return try {
            val parts = timeString.split(":")
            if (parts.size != 2) return null

            val minutes = parts[0].toIntOrNull() ?: return null
            val seconds = parts[1].toIntOrNull() ?: return null

            if (seconds >= 60) return null

            minutes * 60 + seconds
        } catch (e: Exception) {
            null
        }
    }
}
