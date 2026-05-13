package com.marchenaya.mypomodoro.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val workDurationSeconds: Int = 25 * 60,
    val shortBreakDurationSeconds: Int = 5 * 60,
    val longBreakDurationSeconds: Int = 15 * 60,
    val sessionsBeforeLongBreak: Int = 4
)
