package com.marchenaya.mypomodoro.domain.repository

import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.model.TimerState
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
data class PersistentTimerState(
    val sessionType: SessionType,
    val timerState: TimerState,
    val remainingSeconds: Int,
    val totalSeconds: Int,
    val endTime: Long,
    val completedWorkSessions: Int
)

interface TimerRepository {
    val timerStateFlow: Flow<PersistentTimerState>
    suspend fun saveTimerState(state: PersistentTimerState)
}
