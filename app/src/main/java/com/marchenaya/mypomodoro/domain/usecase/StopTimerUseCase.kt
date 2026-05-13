package com.marchenaya.mypomodoro.domain.usecase

import com.marchenaya.mypomodoro.domain.repository.TimerRepository

class StopTimerUseCase(private val repository: TimerRepository) {
    operator fun invoke() = repository.stopTimer()
}
