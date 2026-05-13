package com.marchenaya.mypomodoro.domain.usecase

import com.marchenaya.mypomodoro.domain.model.TimerStatus
import com.marchenaya.mypomodoro.domain.repository.SettingsRepository
import com.marchenaya.mypomodoro.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetTimerStatusUseCase(
    private val timerRepository: TimerRepository,
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke(): Flow<TimerStatus> = combine(
        timerRepository.timerStateFlow,
        settingsRepository.workDurationFlow,
        settingsRepository.shortBreakDurationFlow,
        settingsRepository.longBreakDurationFlow,
        settingsRepository.sessionsBeforeLongBreakFlow
    ) { timerState, work, short, long, sessions ->
        TimerStatus(timerState, work, short, long, sessions)
    }

}
