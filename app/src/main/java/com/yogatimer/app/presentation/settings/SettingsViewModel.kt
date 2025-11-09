package com.yogatimer.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogatimer.app.domain.audio.SoundManager
import com.yogatimer.app.domain.model.Settings
import com.yogatimer.app.domain.model.Theme
import com.yogatimer.app.domain.usecase.settings.GetSettingsUseCase
import com.yogatimer.app.domain.usecase.settings.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Settings Screen.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val soundManager: SoundManager
) : ViewModel() {

    private val _settings = MutableStateFlow(Settings())
    val settings: StateFlow<Settings> = _settings.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            getSettingsUseCase().collect { settings ->
                _settings.value = settings
            }
        }
    }

    fun updateKeepScreenOn(enabled: Boolean) {
        updateSettings { it.copy(keepScreenOn = enabled) }
    }

    fun updateTheme(theme: Theme) {
        updateSettings { it.copy(theme = theme) }
    }

    fun updateEnableSoundEffects(enabled: Boolean) {
        updateSettings { it.copy(enableSoundEffects = enabled) }
    }

    fun updateSoundVolume(volume: Float) {
        updateSettings { it.copy(soundVolume = volume) }
    }

    fun updateCompletionSound(soundUri: String) {
        updateSettings { it.copy(completionSoundUri = soundUri) }
    }

    fun updateEnableTTS(enabled: Boolean) {
        updateSettings { it.copy(enableTTS = enabled) }
    }

    fun updateTTSLanguage(language: String) {
        updateSettings { it.copy(ttsLanguage = language) }
    }

    fun updateEnableVibration(enabled: Boolean) {
        updateSettings { it.copy(enableVibration = enabled) }
    }

    fun updateShowLockScreenControls(enabled: Boolean) {
        updateSettings { it.copy(showLockScreenControls = enabled) }
    }

    fun testSound() {
        viewModelScope.launch {
            soundManager.playTimerCompletionSound()
        }
    }

    private fun updateSettings(update: (Settings) -> Settings) {
        viewModelScope.launch {
            val updatedSettings = update(_settings.value)
            updateSettingsUseCase(updatedSettings)
        }
    }
}
