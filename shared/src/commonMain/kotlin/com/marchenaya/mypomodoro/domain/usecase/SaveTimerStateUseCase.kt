package com.marchenaya.mypomodoro.domain.usecase

import com.marchenaya.mypomodoro.domain.model.PersistentTimerState
import com.marchenaya.mypomodoro.domain.repository.TimerRepository

class SaveTimerStateUseCase(private val repository: TimerRepository) {
    suspend operator fun invoke(state: PersistentTimerState) = repository.saveTimerState(state)
}
