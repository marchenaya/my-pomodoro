package com.marchenaya.mypomodoro.domain.usecase

import com.marchenaya.mypomodoro.domain.model.PersistentTimerState
import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.model.TimerState
import com.marchenaya.mypomodoro.domain.repository.TimerController
import kotlinx.coroutines.flow.first

class StartNextStepUseCase(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val getTimerStateUseCase: GetTimerStateUseCase,
    private val saveTimerStateUseCase: SaveTimerStateUseCase,
    private val timerController: TimerController,
) {
    suspend operator fun invoke() {
        val state = getTimerStateUseCase().first()
        val currentType = state.sessionType
        val completedSessions = if (currentType == SessionType.WORK) {
            state.completedWorkSessions + 1
        } else {
            state.completedWorkSessions
        }

        val sessionsBeforeLongBreak = getSettingsUseCase.sessionsBeforeLongBreak.first()

        val nextType = when (currentType) {
            SessionType.WORK -> {
                if (completedSessions >= sessionsBeforeLongBreak) {
                    SessionType.LONG_BREAK
                } else {
                    SessionType.SHORT_BREAK
                }
            }

            SessionType.SHORT_BREAK, SessionType.LONG_BREAK -> SessionType.WORK
        }

        val nextCompletedSessions =
            if (nextType == SessionType.LONG_BREAK || (currentType == SessionType.LONG_BREAK)) {
                0
            } else {
                completedSessions
            }

        val duration = when (nextType) {
            SessionType.WORK -> getSettingsUseCase.workDuration.first()
            SessionType.SHORT_BREAK -> getSettingsUseCase.shortBreakDuration.first()
            SessionType.LONG_BREAK -> getSettingsUseCase.longBreakDuration.first()
        }

        val endTime = currentTimeMillis() + (duration * MILLIS_IN_SECOND)
        val newState = PersistentTimerState(
            sessionType = nextType,
            timerState = TimerState.RUNNING,
            remainingSeconds = duration,
            totalSeconds = duration,
            endTime = endTime,
            completedWorkSessions = nextCompletedSessions
        )
        saveTimerStateUseCase(newState)
        timerController.start(duration, endTime)
    }

    private fun currentTimeMillis(): Long =
        com.marchenaya.mypomodoro.data.platform.currentTimeMillis()

    companion object {
        private const val MILLIS_IN_SECOND = 1000L
    }
}
