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


    fun incrementSteps(steps: Int = 1) {
        val today = LocalDate.now().toEpochDay()
        CoroutineScope(Dispatchers.IO).launch {
            val savedSteps = stepsDao.getLatestSteps(today) ?: 0
            stepsDao.insertSteps(StepsEntity(today, steps + savedSteps))
        }
    }

    fun updateSteps(steps: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            stepsDao.insertSteps(StepsEntity(LocalDate.now().toEpochDay(), steps))
        }
    }

    fun observeSteps(epochDay: Long): Flow<Int> {
        return stepsDao.getSteps(epochDay).map{
            it ?: 0
        }
    }

    suspend fun getStepsByDate(startDate: LocalDate, endDate: LocalDate): List<StepsEntity> {
        val start = startDate.toEpochDay()
        val end = endDate.toEpochDay()
        return stepsDao.getStepsByDate(start, end)
    }

}