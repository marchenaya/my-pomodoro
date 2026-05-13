package com.marchenaya.mypomodoro.data.mapper

import com.marchenaya.mypomodoro.data.model.PersistentTimerStateSerializable
import com.marchenaya.mypomodoro.data.model.SettingsSerializable
import com.marchenaya.mypomodoro.domain.model.PersistentTimerState
import com.marchenaya.mypomodoro.domain.model.Settings

fun PersistentTimerState.toPersistentTimerStateSerializable(): PersistentTimerStateSerializable {
    return PersistentTimerStateSerializable(
        sessionType = sessionType,
        timerState = timerState,
        remainingSeconds = remainingSeconds,
        totalSeconds = totalSeconds,
        endTime = endTime,
        completedWorkSessions = completedWorkSessions
    )
}

fun PersistentTimerStateSerializable.toPersistentTimerState(): PersistentTimerState {
    return PersistentTimerState(
        sessionType = sessionType,
        timerState = timerState,
        remainingSeconds = remainingSeconds,
        totalSeconds = totalSeconds,
        endTime = endTime,
        completedWorkSessions = completedWorkSessions
    )
}