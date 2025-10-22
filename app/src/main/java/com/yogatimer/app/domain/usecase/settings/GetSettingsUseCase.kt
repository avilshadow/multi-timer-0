package com.yogatimer.app.domain.usecase.settings

import com.yogatimer.app.domain.model.Settings
import com.yogatimer.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<Settings> {
        return repository.getSettings()
    }
}
