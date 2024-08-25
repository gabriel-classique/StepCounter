package com.xcvi.stepcounter.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface StepsDao {

    @Query("select * from stepsentity")
    fun getAllSteps(): Flow<List<StepsEntity>>

    @Query("select steps from stepsentity where epochDay = :epochDay")
    fun getStepsOfDay(epochDay: Long): Flow<Int?>

    @Query("select steps from stepsentity where epochDay = :epochDay")
    suspend fun getLatestStepsOfDay(epochDay: Long): Int?

    @Upsert
    suspend fun updateSteps(stepsEntity: StepsEntity)

}