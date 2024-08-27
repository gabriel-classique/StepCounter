package com.xcvi.stepcounter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StepsEntity(
    @PrimaryKey val epochDay: Long,
    val steps: Int
)