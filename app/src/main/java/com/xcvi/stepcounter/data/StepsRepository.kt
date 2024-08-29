package com.xcvi.stepcounter.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class StepsRepository(
    private val stepsDao: StepsDao
) {
    suspend fun stepsListener(steps: Int) {
        stepsDao.incrementSteps(steps)
    }

    suspend fun resetCounter() {
        stepsDao.updateStepsSinceBoot(StepsSinceBootEntity(id = 1, 0))
    }

    suspend fun updateSteps(steps: Int) {
        stepsDao.updateSteps(StepsEntity(LocalDate.now().toEpochDay(), steps))
    }

    fun observeSteps(epochDay: Long): Flow<Int> {
        return stepsDao.getSteps(epochDay).map {
            it ?: 0
        }
    }

    suspend fun getStepsByDate(startDate: LocalDate, endDate: LocalDate): List<StepsEntity> {
        val start = startDate.toEpochDay()
        val end = endDate.toEpochDay()
        return stepsDao.getStepsByDate(start, end)
    }

}