package com.marchenaya.mypomodoro.domain.repository

import com.marchenaya.mypomodoro.domain.model.SessionType

interface TimerController {
    fun start(remainingSeconds: Int, endTime: Long, sessionType: SessionType)
    fun stop()
}
