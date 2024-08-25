package com.xcvi.stepcounter.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RebootBroadcastReceiver: BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        Intent(context, SensorService::class.java).also {
            it.action = SensorService.Actions.START.name
            context?.startForegroundService(it)
        }
    }

}