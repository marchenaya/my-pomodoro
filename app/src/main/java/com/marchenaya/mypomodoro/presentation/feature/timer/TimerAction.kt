package com.marchenaya.mypomodoro.presentation.feature.timer

import com.marchenaya.mypomodoro.domain.model.SessionType

sealed interface TimerAction {
    data class SetSessionType(val sessionType: SessionType) : TimerAction
    data object StartTimer : TimerAction
    data object PauseTimer : TimerAction
    data object ResetTimer : TimerAction
    data object OnSettingsClick : TimerAction
}
