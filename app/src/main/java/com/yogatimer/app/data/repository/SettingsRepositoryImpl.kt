package com.yogatimer.app.data.repository

import com.yogatimer.app.data.local.database.dao.SettingsDao
import com.yogatimer.app.data.mapper.toDomainModel
import com.yogatimer.app.data.mapper.toEntity
import com.yogatimer.app.domain.model.Settings
import com.yogatimer.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao
) : SettingsRepository {

    override fun getSettings(): Flow<Settings> {
        return settingsDao.getSettings().map { entity ->
            entity?.toDomainModel() ?: Settings() // Return default if null
        }
    }

    override suspend fun getSettingsSync(): Settings {
        return settingsDao.getSettingsSync()?.toDomainModel() ?: Settings()
    }

    override suspend fun saveSettings(settings: Settings) {
        settingsDao.saveSettings(settings.toEntity())
    }
}
