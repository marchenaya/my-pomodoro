package com.marchenaya.mypomodoro.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PersistentTimerStateSerializable(
    val sessionType: SessionTypeSerializable,
    val timerState: TimerStateSerializable,
    val remainingSeconds: Int,
    val totalSeconds: Int,
    val endTime: Long,
    val completedWorkSessions: Int
)