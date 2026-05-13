package com.marchenaya.mypomodoro.domain.usecase

import com.marchenaya.mypomodoro.domain.model.PersistentTimerState
import com.marchenaya.mypomodoro.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow

class GetTimerStateUseCase(private val repository: TimerRepository) {
    operator fun invoke(): Flow<PersistentTimerState> = repository.timerStateFlow
}
