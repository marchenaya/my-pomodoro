package com.marchenaya.mypomodoro.data.mapper

import com.marchenaya.mypomodoro.data.model.SettingsSerializable
import com.marchenaya.mypomodoro.domain.model.Settings

fun Settings.toSettingsSerializable(): SettingsSerializable {
    return SettingsSerializable(
        workDurationSeconds = workDurationSeconds,
        shortBreakDurationSeconds = shortBreakDurationSeconds,
        longBreakDurationSeconds = longBreakDurationSeconds,
        sessionsBeforeLongBreak = sessionsBeforeLongBreak
    )
}

fun SettingsSerializable.toSettings(): Settings {
    return Settings(
        workDurationSeconds = workDurationSeconds,
        shortBreakDurationSeconds = shortBreakDurationSeconds,
        longBreakDurationSeconds = longBreakDurationSeconds,
        sessionsBeforeLongBreak = sessionsBeforeLongBreak
    )
}