package com.marchenaya.mypomodoro.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class TimerStateSerializable {
    IDLE, RUNNING, PAUSED
}
