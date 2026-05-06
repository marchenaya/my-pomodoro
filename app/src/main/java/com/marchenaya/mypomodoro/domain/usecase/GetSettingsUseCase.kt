package com.marchenaya.mypomodoro.domain.usecase

import com.marchenaya.mypomodoro.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetSettingsUseCase(private val repository: SettingsRepository) {
    val workDuration: Flow<Int> = repository.workDurationFlow
    val shortBreakDuration: Flow<Int> = repository.shortBreakDurationFlow
    val longBreakDuration: Flow<Int> = repository.longBreakDurationFlow
    val sessionsBeforeLongBreak: Flow<Int> = repository.sessionsBeforeLongBreakFlow
}