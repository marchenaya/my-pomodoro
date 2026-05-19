package com.marchenaya.mypomodoro.presentation.feature.timer

sealed interface TimerEvent {
    data object NavigateToSettings : TimerEvent
}
