package com.yogatimer.app.domain.usecase.settings

import com.yogatimer.app.domain.model.Settings
import com.yogatimer.app.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(settings: Settings) {
        // Validate volume is in range
        require(settings.soundVolume in 0f..1f) { "Volume must be between 0.0 and 1.0" }

        repository.saveSettings(settings)
    }
}
