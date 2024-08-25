package com.xcvi.stepcounter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class StepsEntity(
    @PrimaryKey val epochDay: Long,
    val steps: Int
) {
}