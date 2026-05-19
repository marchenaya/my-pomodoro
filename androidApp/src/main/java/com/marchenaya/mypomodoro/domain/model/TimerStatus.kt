package com.marchenaya.mypomodoro.domain.model

data class TimerStatus(
    val persistentTimerState: PersistentTimerState,
    val workDuration: Int,
    val shortBreakDuration: Int,
    val longBreakDuration: Int,
    val sessionsBeforeLongBreak: Int
)
