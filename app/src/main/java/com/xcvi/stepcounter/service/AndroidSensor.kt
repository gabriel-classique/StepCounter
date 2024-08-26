package com.xcvi.stepcounter.service

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

abstract class MeasurableSensor(
    protected val sensorType: Int
) {
    protected var onSensorValuesChange: ((List<Float>, Long) -> Unit)? = null

    abstract val detected: Boolean

    abstract fun startListening()
    abstract fun stopListening()

    fun setOnSensorValuesChangeListener(listener: (List<Float>, Long) -> Unit) {
        onSensorValuesChange = listener
    }
}

abstract class AndroidSensor(
    private val context: Context,
    private val sensorFeature: String,
    sensorType: Int
) : MeasurableSensor(sensorType), SensorEventListener {

    override val detected: Boolean
        get() = context.packageManager.hasSystemFeature(sensorFeature)

    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null

    override fun startListening() {
        if (!detected) {
            return
        }
        if (!::sensorManager.isInitialized && sensor == null) {
            sensorManager = context.getSystemService(SensorManager::class.java) as SensorManager
            sensor = sensorManager.getDefaultSensor(sensorType)
        }
        sensor?.let {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun stopListening() {
        if (!::sensorManager.isInitialized || !detected) {
            return
        }
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!detected) {
            return
        }
        if (event?.sensor?.type == sensorType) {
            onSensorValuesChange?.invoke(event.values.toList(), event.timestamp)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

}

class StepCounterSensor(
    context: Context
) : AndroidSensor(
    context,
    PackageManager.FEATURE_SENSOR_STEP_COUNTER,
    Sensor.TYPE_STEP_COUNTER
)


class StepDetectorSensor(
    context: Context
) : AndroidSensor(
    context,
    PackageManager.FEATURE_SENSOR_STEP_DETECTOR,
    Sensor.TYPE_STEP_DETECTOR
)











