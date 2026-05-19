package com.marchenaya.mypomodoro.presentation.feature.settings

import com.marchenaya.mypomodoro.domain.model.Settings

data class SettingsUiState(
    val workDuration: Int = Settings.DEFAULT_WORK_DURATION,
    val shortBreakDuration: Int = Settings.DEFAULT_SHORT_BREAK_DURATION,
    val longBreakDuration: Int = Settings.DEFAULT_LONG_BREAK_DURATION,
    val sessionsBeforeLongBreak: Int = Settings.DEFAULT_SESSIONS_BEFORE_LONG_BREAK
)
