package com.yogatimer.app.di

import com.yogatimer.app.data.repository.SettingsRepositoryImpl
import com.yogatimer.app.data.repository.WorkoutRepositoryImpl
import com.yogatimer.app.domain.repository.SettingsRepository
import com.yogatimer.app.domain.repository.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(
        impl: WorkoutRepositoryImpl
    ): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository
}
