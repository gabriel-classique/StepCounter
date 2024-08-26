package com.xcvi.stepcounter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StepsSinceBootEntity(
    @PrimaryKey val id: Int = 1,
    val stepsSinceBoot: Int
)
