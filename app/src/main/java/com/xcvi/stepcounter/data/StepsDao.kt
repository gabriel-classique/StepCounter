package com.xcvi.stepcounter.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface StepsDao {

    @Query("select steps from stepsentity where epochDay = :epochDay")
    fun getSteps(epochDay: Long): Flow<Int?>

    @Query("select steps from stepsentity where epochDay = :epochDay")
    suspend fun getLatestSteps(epochDay: Long): Int?

    @Query("select * from stepsentity where epochDay >= :start and epochDay <= :end")
    suspend fun getStepsByDate(start: Long, end: Long): List<StepsEntity>

    @Upsert
    suspend fun insertSteps(stepsEntity: StepsEntity)

}