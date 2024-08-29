package com.xcvi.stepcounter.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RebootBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action){
            Intent.ACTION_REBOOT -> restartService(context)
            Intent.ACTION_BOOT_COMPLETED -> restartService(context)
            Intent.ACTION_LOCKED_BOOT_COMPLETED -> restartService(context)
        }
    }

    private fun restartService(context: Context?){
        Intent(context, SensorService::class.java).also {
            it.action = SensorService.Actions.START.name
            context?.startForegroundService(it)
        }
    }

}