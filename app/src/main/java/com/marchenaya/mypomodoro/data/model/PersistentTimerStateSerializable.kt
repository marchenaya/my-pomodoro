package com.marchenaya.mypomodoro.data.model

import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.model.TimerState
import kotlinx.serialization.Serializable

@Serializable
data class PersistentTimerStateSerializable(
    val sessionType: SessionType,
    val timerState: TimerState,
    val remainingSeconds: Int,
    val totalSeconds: Int,
    val endTime: Long,
    val completedWorkSessions: Int
)