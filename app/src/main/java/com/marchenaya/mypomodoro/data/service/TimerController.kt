package com.marchenaya.mypomodoro.data.service

interface TimerController {
    fun start(remainingSeconds: Int, endTime: Long)
    fun stop()
}
