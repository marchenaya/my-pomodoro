package com.marchenaya.mypomodoro.domain.repository

import com.marchenaya.mypomodoro.domain.model.PersistentTimerState
import kotlinx.coroutines.flow.Flow

interface TimerRepository {
    val timerStateFlow: Flow<PersistentTimerState>
    suspend fun saveTimerState(state: PersistentTimerState)
    fun startTimer(remainingSeconds: Int, endTime: Long)
    fun stopTimer()
}
