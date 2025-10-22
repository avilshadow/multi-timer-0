package com.yogatimer.app.data.mapper

import com.yogatimer.app.data.local.database.entities.SectionEntity
import com.yogatimer.app.data.local.database.entities.TimerEntity
import com.yogatimer.app.data.local.database.entities.WorkoutEntity
import com.yogatimer.app.domain.model.Section
import com.yogatimer.app.domain.model.Timer
import com.yogatimer.app.domain.model.Workout

// Workout Entity -> Domain Model
fun WorkoutEntity.toDomainModel(sections: List<Section> = emptyList()) = Workout(
    id = id,
    name = name,
    description = description,
    sections = sections,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isPreloaded = isPreloaded
)

// Workout Domain Model -> Entity
fun Workout.toEntity() = WorkoutEntity(
    id = id,
    name = name,
    description = description,
    createdAt = createdAt,
    updatedAt = System.currentTimeMillis(),
    isPreloaded = isPreloaded,
    sortOrder = 0
)

// Section Entity -> Domain Model
fun SectionEntity.toDomainModel(
    timers: List<Timer> = emptyList(),
    childSections: List<Section> = emptyList()
) = Section(
    id = id,
    workoutId = workoutId,
    parentSectionId = parentSectionId,
    name = name,
    description = description,
    repeatCount = repeatCount,
    timers = timers,
    childSections = childSections,
    level = level
)

// Section Domain Model -> Entity
fun Section.toEntity() = SectionEntity(
    id = id,
    workoutId = workoutId,
    parentSectionId = parentSectionId,
    name = name,
    description = description,
    repeatCount = repeatCount,
    sortOrder = 0,
    level = level
)

// Timer Entity -> Domain Model
fun TimerEntity.toDomainModel() = Timer(
    id = id,
    sectionId = sectionId,
    name = name,
    description = description,
    durationSeconds = durationSeconds
)

// Timer Domain Model -> Entity
fun Timer.toEntity() = TimerEntity(
    id = id,
    sectionId = sectionId,
    name = name,
    description = description,
    durationSeconds = durationSeconds,
    sortOrder = 0
)
