package com.marchenaya.mypomodoro.domain.usecase

import com.marchenaya.mypomodoro.domain.repository.SettingsRepository

class SaveSettingsUseCase(private val repository: SettingsRepository) {
    suspend fun updateWorkDuration(duration: Int) = repository.updateWorkDuration(duration)
    suspend fun updateShortBreakDuration(duration: Int) = repository.updateShortBreakDuration(duration)
    suspend fun updateLongBreakDuration(duration: Int) = repository.updateLongBreakDuration(duration)
    suspend fun updateSessionsBeforeLongBreak(count: Int) = repository.updateSessionsBeforeLongBreak(count)
}