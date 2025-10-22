package com.yogatimer.app.data.local.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yogatimer.app.data.local.database.entities.SettingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseCallback(
    private val database: YogaTimerDatabase
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        // Prepopulate database on background thread
        CoroutineScope(Dispatchers.IO).launch {
            prepopulateDatabase()
        }
    }

    private suspend fun prepopulateDatabase() {
        // Insert default settings
        database.settingsDao().saveSettings(SettingsEntity())

        // Insert example workouts
        insertBeginnerYogaFlow()
        insertAdvancedVinyasa()
    }

    private suspend fun insertBeginnerYogaFlow() {
        val workoutDao = database.workoutDao()
        val sectionDao = database.sectionDao()
        val timerDao = database.timerDao()

        // Create workout
        val workoutId = workoutDao.insertWorkout(
            com.yogatimer.app.data.local.database.entities.WorkoutEntity(
                name = "Beginner Yoga Flow",
                description = "Gentle introduction to yoga poses",
                isPreloaded = true,
                sortOrder = 0
            )
        )

        // Warm Up Section
        val warmUpId = sectionDao.insertSection(
            com.yogatimer.app.data.local.database.entities.SectionEntity(
                workoutId = workoutId,
                name = "Warm Up",
                description = "Prepare your body",
                repeatCount = 1,
                sortOrder = 0,
                level = 0
            )
        )

        timerDao.insertTimers(
            listOf(
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = warmUpId,
                    name = "Child's Pose",
                    description = "",
                    durationSeconds = 60,
                    sortOrder = 0
                ),
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = warmUpId,
                    name = "Cat-Cow",
                    description = "",
                    durationSeconds = 30,
                    sortOrder = 1
                ),
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = warmUpId,
                    name = "Downward Dog",
                    description = "",
                    durationSeconds = 45,
                    sortOrder = 2
                )
            )
        )

        // Standing Poses Section
        val standingId = sectionDao.insertSection(
            com.yogatimer.app.data.local.database.entities.SectionEntity(
                workoutId = workoutId,
                name = "Standing Poses",
                description = "Build strength and balance",
                repeatCount = 1,
                sortOrder = 1,
                level = 0
            )
        )

        timerDao.insertTimers(
            listOf(
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = standingId,
                    name = "Mountain Pose",
                    description = "",
                    durationSeconds = 30,
                    sortOrder = 0
                ),
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = standingId,
                    name = "Forward Fold",
                    description = "",
                    durationSeconds = 45,
                    sortOrder = 1
                ),
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = standingId,
                    name = "Tree Pose (Right)",
                    description = "",
                    durationSeconds = 30,
                    sortOrder = 2
                ),
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = standingId,
                    name = "Tree Pose (Left)",
                    description = "",
                    durationSeconds = 30,
                    sortOrder = 3
                )
            )
        )

        // Cool Down Section
        val coolDownId = sectionDao.insertSection(
            com.yogatimer.app.data.local.database.entities.SectionEntity(
                workoutId = workoutId,
                name = "Cool Down",
                description = "",
                repeatCount = 1,
                sortOrder = 2,
                level = 0
            )
        )

        timerDao.insertTimers(
            listOf(
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = coolDownId,
                    name = "Seated Twist",
                    description = "",
                    durationSeconds = 30,
                    sortOrder = 0
                ),
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = coolDownId,
                    name = "Corpse Pose",
                    description = "",
                    durationSeconds = 120,
                    sortOrder = 1
                )
            )
        )
    }

    private suspend fun insertAdvancedVinyasa() {
        val workoutDao = database.workoutDao()
        val sectionDao = database.sectionDao()
        val timerDao = database.timerDao()

        // Create workout
        val workoutId = workoutDao.insertWorkout(
            com.yogatimer.app.data.local.database.entities.WorkoutEntity(
                name = "Advanced Vinyasa",
                description = "Dynamic flow for experienced practitioners",
                isPreloaded = true,
                sortOrder = 1
            )
        )

        // Sun Salutations Section (with repeats)
        val sunSalId = sectionDao.insertSection(
            com.yogatimer.app.data.local.database.entities.SectionEntity(
                workoutId = workoutId,
                name = "Sun Salutations",
                description = "Warm up with sun salutations",
                repeatCount = 3,
                sortOrder = 0,
                level = 0
            )
        )

        timerDao.insertTimers(
            listOf(
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = sunSalId,
                    name = "Forward Fold",
                    description = "",
                    durationSeconds = 15,
                    sortOrder = 0
                ),
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = sunSalId,
                    name = "Plank",
                    description = "",
                    durationSeconds = 20,
                    sortOrder = 1
                ),
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = sunSalId,
                    name = "Chaturanga",
                    description = "",
                    durationSeconds = 10,
                    sortOrder = 2
                ),
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = sunSalId,
                    name = "Upward Dog",
                    description = "",
                    durationSeconds = 15,
                    sortOrder = 3
                ),
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = sunSalId,
                    name = "Downward Dog",
                    description = "",
                    durationSeconds = 20,
                    sortOrder = 4
                )
            )
        )

        // Warrior Flow (parent section with nested sections)
        val warriorFlowId = sectionDao.insertSection(
            com.yogatimer.app.data.local.database.entities.SectionEntity(
                workoutId = workoutId,
                name = "Warrior Flow",
                description = "Build strength and stamina",
                repeatCount = 2,
                sortOrder = 1,
                level = 0
            )
        )

        // Right Side (nested section)
        val rightSideId = sectionDao.insertSection(
            com.yogatimer.app.data.local.database.entities.SectionEntity(
                workoutId = workoutId,
                parentSectionId = warriorFlowId,
                name = "Right Side",
                description = "",
                repeatCount = 1,
                sortOrder = 0,
                level = 1
            )
        )

        timerDao.insertTimers(
            listOf(
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = rightSideId,
                    name = "Warrior I",
                    description = "",
                    durationSeconds = 45,
                    sortOrder = 0
                ),
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = rightSideId,
                    name = "Warrior II",
                    description = "",
                    durationSeconds = 45,
                    sortOrder = 1
                ),
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = rightSideId,
                    name = "Triangle",
                    description = "",
                    durationSeconds = 30,
                    sortOrder = 2
                )
            )
        )

        // Left Side (nested section)
        val leftSideId = sectionDao.insertSection(
            com.yogatimer.app.data.local.database.entities.SectionEntity(
                workoutId = workoutId,
                parentSectionId = warriorFlowId,
                name = "Left Side",
                description = "",
                repeatCount = 1,
                sortOrder = 1,
                level = 1
            )
        )

        timerDao.insertTimers(
            listOf(
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = leftSideId,
                    name = "Warrior I",
                    description = "",
                    durationSeconds = 45,
                    sortOrder = 0
                ),
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = leftSideId,
                    name = "Warrior II",
                    description = "",
                    durationSeconds = 45,
                    sortOrder = 1
                ),
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = leftSideId,
                    name = "Triangle",
                    description = "",
                    durationSeconds = 30,
                    sortOrder = 2
                )
            )
        )

        // Cool Down Section
        val coolDownId = sectionDao.insertSection(
            com.yogatimer.app.data.local.database.entities.SectionEntity(
                workoutId = workoutId,
                name = "Cool Down",
                description = "",
                repeatCount = 1,
                sortOrder = 2,
                level = 0
            )
        )

        timerDao.insertTimers(
            listOf(
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = coolDownId,
                    name = "Pigeon Pose",
                    description = "",
                    durationSeconds = 60,
                    sortOrder = 0
                ),
                com.yogatimer.app.data.local.database.entities.TimerEntity(
                    sectionId = coolDownId,
                    name = "Savasana",
                    description = "",
                    durationSeconds = 180,
                    sortOrder = 1
                )
            )
        )
    }
}
