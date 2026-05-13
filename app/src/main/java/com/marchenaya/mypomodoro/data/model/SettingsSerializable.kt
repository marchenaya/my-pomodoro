package com.marchenaya.mypomodoro.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SettingsSerializable(
    val workDurationSeconds: Int = 25 * 60,
    val shortBreakDurationSeconds: Int = 5 * 60,
    val longBreakDurationSeconds: Int = 15 * 60,
    val sessionsBeforeLongBreak: Int = 4
)
