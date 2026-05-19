package com.marchenaya.mypomodoro.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val workDurationFlow: Flow<Int>
    val shortBreakDurationFlow: Flow<Int>
    val longBreakDurationFlow: Flow<Int>
    val sessionsBeforeLongBreakFlow: Flow<Int>
    suspend fun updateWorkDuration(duration: Int)
    suspend fun updateShortBreakDuration(duration: Int)
    suspend fun updateLongBreakDuration(duration: Int)
    suspend fun updateSessionsBeforeLongBreak(count: Int)
}