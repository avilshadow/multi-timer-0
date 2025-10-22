package com.yogatimer.app.di

import android.content.Context
import androidx.room.Room
import com.yogatimer.app.data.local.database.DatabaseCallback
import com.yogatimer.app.data.local.database.YogaTimerDatabase
import com.yogatimer.app.data.local.database.dao.SectionDao
import com.yogatimer.app.data.local.database.dao.SettingsDao
import com.yogatimer.app.data.local.database.dao.TimerDao
import com.yogatimer.app.data.local.database.dao.WorkoutDao
import com.yogatimer.app.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideYogaTimerDatabase(
        @ApplicationContext context: Context
    ): YogaTimerDatabase {
        val database = Room.databaseBuilder(
            context,
            YogaTimerDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

        // Add callback for prepopulation
        val callbackDatabase = Room.databaseBuilder(
            context,
            YogaTimerDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .addCallback(DatabaseCallback(database))
            .fallbackToDestructiveMigration()
            .build()

        return callbackDatabase
    }

    @Provides
    fun provideWorkoutDao(database: YogaTimerDatabase): WorkoutDao {
        return database.workoutDao()
    }

    @Provides
    fun provideSectionDao(database: YogaTimerDatabase): SectionDao {
        return database.sectionDao()
    }

    @Provides
    fun provideTimerDao(database: YogaTimerDatabase): TimerDao {
        return database.timerDao()
    }

    @Provides
    fun provideSettingsDao(database: YogaTimerDatabase): SettingsDao {
        return database.settingsDao()
    }
}
