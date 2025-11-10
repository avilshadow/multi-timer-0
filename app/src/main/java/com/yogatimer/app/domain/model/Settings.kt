package com.yogatimer.app.domain.model

data class Settings(
    // Display Settings
    val keepScreenOn: Boolean = true,
    val theme: Theme = Theme.SYSTEM,

    // Audio Settings
    val enableTTS: Boolean = true,
    val ttsLanguage: String = "en-US",
    val enableSoundEffects: Boolean = true,
    val completionSoundUri: String = "ding",
    val soundVolume: Float = 0.7f, // 0.0 to 1.0

    // Haptic Settings
    val enableVibration: Boolean = true,

    // Notification Settings
    val showLockScreenControls: Boolean = true
)

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM;

    companion object {
        fun fromString(value: String): Theme {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                SYSTEM
            }
        }
    }
}
