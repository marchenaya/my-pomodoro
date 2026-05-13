package com.marchenaya.mypomodoro.presentation.feature.settings

data class SettingsUiState(
    val workDuration: Int = 25 * 60,
    val shortBreakDuration: Int = 5 * 60,
    val longBreakDuration: Int = 15 * 60,
    val sessionsBeforeLongBreak: Int = 4
)
