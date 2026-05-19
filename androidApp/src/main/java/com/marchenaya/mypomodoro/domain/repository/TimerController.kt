package com.marchenaya.mypomodoro.domain.repository

interface TimerController {
    fun start(remainingSeconds: Int, endTime: Long)
    fun stop()
}
