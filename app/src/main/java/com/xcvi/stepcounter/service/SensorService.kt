package com.xcvi.stepcounter.service


import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.xcvi.stepcounter.R
import com.xcvi.stepcounter.data.StepsRepository
import com.xcvi.stepcounter.service.sensor.AccelerometerSensor
import com.xcvi.stepcounter.service.sensor.StepListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SensorService : LifecycleService() {

    @Inject
    lateinit var repository: StepsRepository

    @Inject
    lateinit var sensor: AccelerometerSensor

    override fun onCreate() {
        super.onCreate()
        if (hasPermissions(this)) {
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.name -> {
                if (hasPermissions(this)) {
                    start()
                }
            }
            Actions.RESTART.name -> {
                lifecycleScope.launch {
                    repository.resetCounter()
                }
                if (hasPermissions(this)) {
                    start()
                }
            }
            Actions.STOP.name -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {

        val notification =  if (isRunning) {
            notyBuilder(title = "Step Counter is Active")
        } else {
            notyBuilder(title = "Step Counter is NOT detected.")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(SERVICE_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH)
        } else {
            startForeground(SERVICE_ID, notification)
        }

        sensor.registerListener(object : StepListener {
            override fun onStep(count: Int) {
                lifecycleScope.launch {
                    repository.stepsListener(count)
                }
            }
        })

        isRunning = true
    }

    private fun stop() {
        sensor.unregisterListener()
        stopSelf()
        isRunning = false
    }

    enum class Actions { START, STOP, RESTART}

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "sensor_service_channel_id"
        const val NOTIFICATION_NAME = "Service Name"
        const val SERVICE_ID = 11111

        var isRunning by mutableStateOf(false)
            private set

        fun hasPermissions(context: Context): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                return true
            } else {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACTIVITY_RECOGNITION
                            ) == PackageManager.PERMISSION_GRANTED
                } else {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ) == PackageManager.PERMISSION_GRANTED
                }
            }
        }
    }

    private fun notyBuilder(
        title: String,
        body: String = "",
    ): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            this.packageManager.getLaunchIntentForPackage(this.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .build()
    }
}

