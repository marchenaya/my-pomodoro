package com.marchenaya.mypomodoro.data.service

import com.marchenaya.mypomodoro.domain.repository.TimerController

class TimerControllerImpl(
    private val commonTimerManager: CommonTimerManager
) : TimerController {

    override fun start(remainingSeconds: Int, endTime: Long) {
        commonTimerManager.start(remainingSeconds, endTime)
    }

    override fun stop() {
        commonTimerManager.stop()
    }

}
