package com.xcvi.stepcounter.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.xcvi.stepcounter.data.StepsRepository
import javax.inject.Inject

class RebootBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action){
            Intent.ACTION_REBOOT -> restartService(context, intent)
            Intent.ACTION_BOOT_COMPLETED -> restartService(context, intent)
            Intent.ACTION_LOCKED_BOOT_COMPLETED -> restartService(context, intent)
        }
    }

    private fun restartService(context: Context?, intent: Intent?){
        Intent(context, SensorService::class.java).also {
            it.action = SensorService.Actions.RESTART.name
            context?.startForegroundService(it)
        }
    }

}