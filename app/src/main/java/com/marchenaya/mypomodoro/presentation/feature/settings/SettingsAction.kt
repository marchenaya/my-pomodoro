package com.marchenaya.mypomodoro.presentation.feature.settings

sealed interface SettingsAction {
    data class UpdateWorkDuration(val durationSeconds: Int) : SettingsAction
    data class UpdateShortBreakDuration(val durationSeconds: Int) : SettingsAction
    data class UpdateLongBreakDuration(val durationSeconds: Int) : SettingsAction
    data class UpdateSessionsBeforeLongBreak(val count: Int) : SettingsAction
}
