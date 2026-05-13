package com.marchenaya.mypomodoro.domain.usecase

import com.marchenaya.mypomodoro.domain.repository.TimerController

class StopTimerUseCase(private val controller: TimerController) {
    operator fun invoke() = controller.stop()
}
