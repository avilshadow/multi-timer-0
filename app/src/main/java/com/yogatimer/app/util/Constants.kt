package com.yogatimer.app.util

object Constants {
    // Database
    const val DATABASE_NAME = "yoga_timer_db"
    const val DATABASE_VERSION = 1

    // Notification
    const val NOTIFICATION_CHANNEL_ID = "timer_channel"
    const val NOTIFICATION_ID = 1001

    // Intent Actions
    const val ACTION_PAUSE = "com.yogatimer.app.PAUSE"
    const val ACTION_RESUME = "com.yogatimer.app.RESUME"
    const val ACTION_SKIP = "com.yogatimer.app.SKIP"
    const val ACTION_STOP = "com.yogatimer.app.STOP"

    // Repeat Limits
    const val MIN_REPEAT_COUNT = 1
    const val MAX_REPEAT_COUNT = 99
    const val MAX_NESTING_LEVEL = 2

    // Timer Limits
    const val MIN_DURATION_SECONDS = 1
    const val MAX_DURATION_SECONDS = 5940 // 99 minutes

    // Settings Defaults
    const val DEFAULT_TTS_LANGUAGE = "en-US"
    const val DEFAULT_SOUND_VOLUME = 0.7f
    const val DEFAULT_THEME = "SYSTEM"
}
