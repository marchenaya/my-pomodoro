package com.marchenaya.mypomodoro.data.service

import android.content.Context
import android.content.Intent
import android.os.Build

import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.repository.TimerController

class TimerControllerImpl(private val context: Context) : TimerController {

    override fun start(remainingSeconds: Int, endTime: Long, sessionType: SessionType) {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_START
            putExtra(TimerService.EXTRA_REMAINING_SECONDS, remainingSeconds)
            putExtra(TimerService.EXTRA_END_TIME, endTime)
            putExtra(TimerService.EXTRA_SESSION_TYPE, sessionType.name)
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
