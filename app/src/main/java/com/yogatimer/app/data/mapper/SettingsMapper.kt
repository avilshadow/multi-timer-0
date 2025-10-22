package com.yogatimer.app.data.mapper

import com.yogatimer.app.data.local.database.entities.SettingsEntity
import com.yogatimer.app.domain.model.Settings
import com.yogatimer.app.domain.model.Theme

// Settings Entity -> Domain Model
fun SettingsEntity.toDomainModel() = Settings(
    keepScreenOn = keepScreenOn,
    theme = Theme.fromString(theme),
    enableTTS = enableTTS,
    ttsLanguage = ttsLanguage,
    enableSoundEffects = enableSoundEffects,
    completionSoundUri = completionSoundUri,
    soundVolume = soundVolume,
    enableVibration = enableVibration,
    showLockScreenControls = showLockScreenControls
)

// Settings Domain Model -> Entity
fun Settings.toEntity() = SettingsEntity(
    id = 1,
    keepScreenOn = keepScreenOn,
    theme = theme.name,
    enableTTS = enableTTS,
    ttsLanguage = ttsLanguage,
    enableSoundEffects = enableSoundEffects,
    completionSoundUri = completionSoundUri,
    soundVolume = soundVolume,
    enableVibration = enableVibration,
    showLockScreenControls = showLockScreenControls
)
