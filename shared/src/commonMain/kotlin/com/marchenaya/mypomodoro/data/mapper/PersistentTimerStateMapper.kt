package com.marchenaya.mypomodoro.data.mapper

import com.marchenaya.mypomodoro.data.model.PersistentTimerStateSerializable
import com.marchenaya.mypomodoro.domain.model.PersistentTimerState

fun PersistentTimerState.toPersistentTimerStateSerializable(): PersistentTimerStateSerializable {
    return PersistentTimerStateSerializable(
        sessionType = sessionType.toSessionTypeSerializable(),
        timerState = timerState.toTimerStateSerializable(),
        remainingSeconds = remainingSeconds,
        totalSeconds = totalSeconds,
        endTime = endTime,
        completedWorkSessions = completedWorkSessions
    )
}

fun PersistentTimerStateSerializable.toPersistentTimerState(): PersistentTimerState {
    return PersistentTimerState(
        sessionType = sessionType.toSessionType(),
        timerState = timerState.toTimerState(),
        remainingSeconds = remainingSeconds,
        totalSeconds = totalSeconds,
        endTime = endTime,
        completedWorkSessions = completedWorkSessions
    )
}