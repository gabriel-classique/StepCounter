package com.xcvi.stepcounter.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate

class StepsRepository(
    private val stepsDao: StepsDao
) {
    suspend fun stepsListener(steps: Float) {
        val today = LocalDate.now().toEpochDay()
        val counterSteps = steps.toInt()
        val stepsSinceBoot = stepsDao.getStepsSinceBoot() ?: 0
        val delta = if (counterSteps - stepsSinceBoot >= 0) {
            counterSteps - stepsSinceBoot
        } else {
            counterSteps
        }

        val savedSteps = stepsDao.getLatestSteps(today) ?: 0
        stepsDao.updateSteps(
            StepsEntity(
                today,
                savedSteps + delta
            )
        )
        stepsDao.updateStepsSinceBoot(
            StepsSinceBootEntity(
                id = 1,
                stepsSinceBoot = counterSteps
            )
        )
    }

    suspend fun resetCount() {
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