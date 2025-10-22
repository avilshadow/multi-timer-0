package com.yogatimer.app.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    val id: Int = 1, // Always 1 (singleton)

    // Display Settings
    val keepScreenOn: Boolean = true,
    val theme: String = "SYSTEM", // LIGHT, DARK, SYSTEM

    // Audio Settings
    val enableTTS: Boolean = true,
    val ttsLanguage: String = "en-US",
    val enableSoundEffects: Boolean = true,
    val completionSoundUri: String = "system_default",
    val soundVolume: Float = 0.7f, // 0.0 to 1.0

    // Haptic Settings
    val enableVibration: Boolean = true,

    // Notification Settings
    val showLockScreenControls: Boolean = true
)
