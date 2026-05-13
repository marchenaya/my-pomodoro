package com.marchenaya.mypomodoro.data.service

import android.content.Context
import android.content.Intent
import android.os.Build

class TimerControllerImpl(private val context: Context) : TimerController {

    override fun start(remainingSeconds: Int, endTime: Long) {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_START
            putExtra(TimerService.EXTRA_REMAINING_SECONDS, remainingSeconds)
            putExtra(TimerService.EXTRA_END_TIME, endTime)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    override fun stop() {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_STOP
        }
        context.startService(intent)
    }

}
