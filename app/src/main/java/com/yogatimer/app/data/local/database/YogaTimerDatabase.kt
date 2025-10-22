package com.yogatimer.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yogatimer.app.data.local.database.dao.SectionDao
import com.yogatimer.app.data.local.database.dao.SettingsDao
import com.yogatimer.app.data.local.database.dao.TimerDao
import com.yogatimer.app.data.local.database.dao.WorkoutDao
import com.yogatimer.app.data.local.database.entities.SectionEntity
import com.yogatimer.app.data.local.database.entities.SettingsEntity
import com.yogatimer.app.data.local.database.entities.TimerEntity
import com.yogatimer.app.data.local.database.entities.WorkoutEntity

@Database(
    entities = [
        WorkoutEntity::class,
        SectionEntity::class,
        TimerEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class YogaTimerDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun sectionDao(): SectionDao
    abstract fun timerDao(): TimerDao
    abstract fun settingsDao(): SettingsDao
}
