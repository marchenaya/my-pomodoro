package com.marchenaya.mypomodoro.presentation.feature.timer

import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.model.TimerState

data class TimerUiState(
    val sessionType: SessionType = SessionType.WORK,
    val timerState: TimerState = TimerState.IDLE,
    val remainingSeconds: Int = 25 * 60,
    val totalSeconds: Int = 25 * 60,
    val completedWorkSessions: Int = 0
)
