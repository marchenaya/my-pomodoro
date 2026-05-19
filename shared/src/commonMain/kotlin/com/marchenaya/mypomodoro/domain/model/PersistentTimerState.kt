package com.marchenaya.mypomodoro.domain.model

data class PersistentTimerState(
    val sessionType: SessionType,
    val timerState: TimerState,
    val remainingSeconds: Int,
    val totalSeconds: Int,
    val endTime: Long,
    val completedWorkSessions: Int
)