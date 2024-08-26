package com.xcvi.stepcounter.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.xcvi.stepcounter.data.StepsRepository
import javax.inject.Inject

class RebootBroadcastReceiver: BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        Intent(context, SensorService::class.java).also {
            it.action = SensorService.Actions.RESTART.name
            context?.startForegroundService(it)
        }
    }

}