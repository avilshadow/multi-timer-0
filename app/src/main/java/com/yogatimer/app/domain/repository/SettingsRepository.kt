package com.yogatimer.app.domain.repository

import com.yogatimer.app.domain.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    /**
     * Get settings as a Flow
     */
    fun getSettings(): Flow<Settings>

    /**
     * Get settings synchronously (one-time read)
     */
    suspend fun getSettingsSync(): Settings

    /**
     * Save settings
     */
    suspend fun saveSettings(settings: Settings)
}
