package com.xcvi.stepcounter.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StepsEntity::class, StepsSinceBootEntity::class], version = 1, exportSchema = false)
abstract class BioDatabase: RoomDatabase() {
    abstract val dao: StepsDao
}