package com.marchenaya.mypomodoro.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class TimerState {
    IDLE, RUNNING, PAUSED
}
