package com.marchenaya.mypomodoro.presentation.feature.timer

import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.model.Settings
import com.marchenaya.mypomodoro.domain.model.TimerState

data class TimerUiState(
    val sessionType: SessionType = SessionType.WORK,
    val timerState: TimerState = TimerState.IDLE,
    val remainingSeconds: Int = Settings.DEFAULT_WORK_DURATION,
    val totalSeconds: Int = Settings.DEFAULT_WORK_DURATION,
    val completedWorkSessions: Int = DEFAULT_COMPLETED_SESSIONS
)

private const val DEFAULT_COMPLETED_SESSIONS = 0
