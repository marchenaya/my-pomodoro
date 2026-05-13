package com.marchenaya.mypomodoro.domain.repository

import com.marchenaya.mypomodoro.domain.model.PersistentTimerState
import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.model.TimerState
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable



interface TimerRepository {
    val timerStateFlow: Flow<PersistentTimerState>
    suspend fun saveTimerState(state: PersistentTimerState)
}
