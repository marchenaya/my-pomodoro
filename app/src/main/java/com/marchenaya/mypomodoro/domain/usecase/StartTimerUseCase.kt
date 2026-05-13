package com.marchenaya.mypomodoro.domain.usecase

import com.marchenaya.mypomodoro.domain.repository.TimerRepository

class StartTimerUseCase(private val repository: TimerRepository) {
    operator fun invoke(remainingSeconds: Int, endTime: Long) =
        repository.startTimer(remainingSeconds, endTime)
}
