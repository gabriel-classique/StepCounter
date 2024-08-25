package com.xcvi.stepcounter

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.xcvi.stepcounter.service.SensorService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StepCounterApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            SensorService.NOTIFICATION_CHANNEL_ID,
            SensorService.NOTIFICATION_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}