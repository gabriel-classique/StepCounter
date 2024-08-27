package com.xcvi.stepcounter.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface StepsDao {

    @Query("select steps from stepsentity where epochDay = :epochDay")
    fun getSteps(epochDay: Long): Flow<Int?>

    @Query("select steps from stepsentity where epochDay = :epochDay")
    suspend fun getLatestSteps(epochDay: Long): Int?

    @Query("select * from stepsentity where epochDay >= :start and epochDay <= :end")
    suspend fun getStepsByDate(start: Long, end: Long): List<StepsEntity>

    @Upsert
    suspend fun updateSteps(stepsEntity: StepsEntity)

    @Transaction
    suspend fun incrementSteps(counterSteps: Int){
        val savedSteps = getLatestSteps(LocalDate.now().toEpochDay()) ?: 0
        val stepsSinceBoot = getStepsSinceBoot() ?: 0
        val delta = if (counterSteps - stepsSinceBoot >= 0) {
            counterSteps - stepsSinceBoot
        } else {
            counterSteps
        }
        updateSteps(
            StepsEntity(
                LocalDate.now().toEpochDay(),
                savedSteps + delta
            )
        )
        updateStepsSinceBoot(
            StepsSinceBootEntity(
                id = 1,
                stepsSinceBoot = counterSteps
            )
        )
    }

    @Upsert
    suspend fun updateStepsSinceBoot(stepsSinceBootEntity: StepsSinceBootEntity)

    @Query("select stepsSinceBoot from stepssincebootentity where id = 1")
    suspend fun getStepsSinceBoot(): Int?

}