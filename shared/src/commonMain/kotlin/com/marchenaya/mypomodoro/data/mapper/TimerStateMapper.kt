package com.marchenaya.mypomodoro.data.mapper

import com.marchenaya.mypomodoro.data.model.TimerStateSerializable
import com.marchenaya.mypomodoro.domain.model.TimerState

fun TimerState.toTimerStateSerializable(): TimerStateSerializable {
    return when (this) {
        TimerState.IDLE -> TimerStateSerializable.IDLE
        TimerState.RUNNING -> TimerStateSerializable.RUNNING
        TimerState.PAUSED -> TimerStateSerializable.PAUSED
    }
}

fun TimerStateSerializable.toTimerState(): TimerState {
    return when (this) {
        TimerStateSerializable.IDLE -> TimerState.IDLE
        TimerStateSerializable.RUNNING -> TimerState.RUNNING
        TimerStateSerializable.PAUSED -> TimerState.PAUSED
    }
}