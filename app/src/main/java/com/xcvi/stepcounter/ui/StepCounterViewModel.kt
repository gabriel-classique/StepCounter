package com.xcvi.stepcounter.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xcvi.stepcounter.data.StepsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StepCounterViewModel @Inject constructor(
    private val stepsRepository: StepsRepository
) : ViewModel() {

    var steps by mutableIntStateOf(0)

    init {
        viewModelScope.launch {
            stepsRepository.getStepsOfDay(LocalDate.now().toEpochDay()).collect{
                steps = it
            }
        }
    }

    fun addSteps(steps: Int = 1) {
        viewModelScope.launch { stepsRepository.incSteps(steps) }
    }

}