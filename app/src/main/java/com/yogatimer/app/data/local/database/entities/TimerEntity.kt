package com.yogatimer.app.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "timers",
    foreignKeys = [
        ForeignKey(
            entity = SectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sectionId")]
)
data class TimerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val sectionId: Long,

    val name: String,
    val description: String = "",
    val durationSeconds: Int,
    val sortOrder: Int = 0
)
