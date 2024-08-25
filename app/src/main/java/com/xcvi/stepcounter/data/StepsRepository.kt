package com.xcvi.stepcounter.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class StepsRepository(
    private val stepsDao: StepsDao
) {

    suspend fun incSteps(steps: Int = 1){
        val stepsOfToday = getLatestSteps(LocalDate.now().toEpochDay())
        stepsDao.updateSteps(
            StepsEntity(
                epochDay = LocalDate.now().toEpochDay(),
                steps = stepsOfToday + steps
            )
        )
    }

    fun getStepsOfDay(epochDay: Long): Flow<Int> {
        return stepsDao.getStepsOfDay(epochDay).map {
            it ?: 0
        }
    }

    suspend fun getLatestSteps(epochDay: Long): Int {
        val steps = stepsDao.getLatestStepsOfDay(LocalDate.now().toEpochDay()) ?: 0
        return steps
    }

}