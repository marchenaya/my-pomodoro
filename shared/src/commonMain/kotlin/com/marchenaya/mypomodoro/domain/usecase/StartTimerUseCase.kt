package com.marchenaya.mypomodoro.domain.usecase

import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.repository.TimerController

class StartTimerUseCase(private val controller: TimerController) {
    operator fun invoke(remainingSeconds: Int, endTime: Long, sessionType: SessionType) =
        controller.start(remainingSeconds, endTime, sessionType)
}
