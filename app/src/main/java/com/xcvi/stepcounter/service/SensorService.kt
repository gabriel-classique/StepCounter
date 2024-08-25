package com.xcvi.stepcounter.service


import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.xcvi.stepcounter.R
import com.xcvi.stepcounter.data.StepsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
class SensorService : Service() {

    @Inject
    lateinit var repository: StepsRepository

    @Inject
    lateinit var sensor: MeasurableSensor

    private var deltaSteps = 0


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

            Actions.STOP.name -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            this.packageManager.getLaunchIntentForPackage(this.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(
                if (isRunning) {
                    "Step Counter is Active"
                } else {
                    "Step Counter is NOT detected."
                }
            )
            .setContentText(
                if (isRunning) {
                    "Steps: $steps"
                } else {
                    ""
                }
            )
            .setContentIntent(pendingIntent)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(SERVICE_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH)
        } else {
            startForeground(SERVICE_ID, notification)
        }
        sensor.startListening()
        sensor.setOnSensorValuesChangeListener { data ->
            CoroutineScope(Dispatchers.IO).launch {
                val savedSteps = repository.getLatestSteps(LocalDate.now().toEpochDay())
                val newSteps = data[0].roundToInt()
                val delta = savedSteps - newSteps
                deltaSteps = if(delta >= 0){
                    delta
                } else {
                    0
                }
                steps = deltaSteps + newSteps - savedSteps
                repository.incSteps(steps)
            }
        }
        isRunning = true
    }

    private fun stop() {
        sensor.stopListening()
        stopSelf()
        isRunning = false
    }

    enum class Actions { START, STOP }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "sensor_service_channel_id"
        const val NOTIFICATION_NAME = "Service Name"
        const val SERVICE_ID = 11111

        var isRunning by mutableStateOf(false)
            private set
        var steps by mutableIntStateOf(0)
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

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

