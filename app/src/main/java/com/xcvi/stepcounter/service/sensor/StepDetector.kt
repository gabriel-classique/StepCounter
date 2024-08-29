package com.xcvi.stepcounter.service.sensor

interface StepDetector {
    fun registerListener(stepListener: StepListener): Boolean
    fun unregisterListener()
}

