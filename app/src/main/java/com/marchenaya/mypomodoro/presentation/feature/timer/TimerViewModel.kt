package com.marchenaya.mypomodoro.presentation.feature.timer

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.model.TimerState
import com.marchenaya.mypomodoro.domain.repository.PersistentTimerState
import com.marchenaya.mypomodoro.domain.usecase.GetSettingsUseCase
import com.marchenaya.mypomodoro.domain.usecase.GetTimerStateUseCase
import com.marchenaya.mypomodoro.domain.usecase.SaveTimerStateUseCase
import com.marchenaya.mypomodoro.service.TimerService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TimerViewModel(
    private val context: Context,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val getTimerStateUseCase: GetTimerStateUseCase,
    private val saveTimerStateUseCase: SaveTimerStateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private val _events = Channel<TimerEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                getTimerStateUseCase(),
                getSettingsUseCase.workDuration,
                getSettingsUseCase.shortBreakDuration,
                getSettingsUseCase.longBreakDuration
            ) { savedState, work, short, long ->
                if (savedState.timerState == TimerState.IDLE) {
                    val duration = when (savedState.sessionType) {
                        SessionType.WORK -> work
                        SessionType.SHORT_BREAK -> short
                        SessionType.LONG_BREAK -> long
                    }
                    savedState.copy(
                        remainingSeconds = duration * 60,
                        totalSeconds = duration * 60
                    )
                } else {
                    savedState
                }
            }.collect { state ->
                _uiState.value = TimerUiState(
                    sessionType = state.sessionType,
                    timerState = state.timerState,
                    remainingSeconds = state.remainingSeconds,
                    totalSeconds = state.totalSeconds,
                    completedWorkSessions = state.completedWorkSessions
                )
            }
        }
    }

    fun onAction(action: TimerAction) {
        when (action) {
            is TimerAction.SetSessionType -> setSessionType(action.sessionType)
            TimerAction.StartTimer -> startTimer()
            TimerAction.PauseTimer -> pauseTimer()
            TimerAction.ResetTimer -> resetTimer()
            TimerAction.OnSettingsClick -> {
                viewModelScope.launch {
                    _events.send(TimerEvent.NavigateToSettings)
                }
            }
        }
    }

    private fun startTimer() {
        if (_uiState.value.timerState == TimerState.RUNNING) return

        val newRemaining =
            if (_uiState.value.remainingSeconds <= 0) _uiState.value.totalSeconds else _uiState.value.remainingSeconds
        _uiState.value = _uiState.value.copy(
            timerState = TimerState.RUNNING,
            remainingSeconds = newRemaining
        )
        val endTime = System.currentTimeMillis() + (newRemaining * 1000L)

        saveState(endTime)
        startService(TimerService.ACTION_START, newRemaining)
    }

    private fun pauseTimer() {
        _uiState.value = _uiState.value.copy(timerState = TimerState.PAUSED)
        saveState()
        startService(TimerService.ACTION_STOP)
    }

    private fun resetTimer() {
        startService(TimerService.ACTION_STOP)
        viewModelScope.launch {
            val duration = when (_uiState.value.sessionType) {
                SessionType.WORK -> getSettingsUseCase.workDuration.first()
                SessionType.SHORT_BREAK -> getSettingsUseCase.shortBreakDuration.first()
                SessionType.LONG_BREAK -> getSettingsUseCase.longBreakDuration.first()
            }
            _uiState.value = _uiState.value.copy(
                timerState = TimerState.IDLE,
                remainingSeconds = duration * 60,
                totalSeconds = duration * 60
            )
            saveState()
        }
    }

    private fun setSessionType(sessionType: SessionType) {
        startService(TimerService.ACTION_STOP)
        viewModelScope.launch {
            val duration = when (sessionType) {
                SessionType.WORK -> getSettingsUseCase.workDuration.first()
                SessionType.SHORT_BREAK -> getSettingsUseCase.shortBreakDuration.first()
                SessionType.LONG_BREAK -> getSettingsUseCase.longBreakDuration.first()
            }
            _uiState.value = _uiState.value.copy(
                sessionType = sessionType,
                timerState = TimerState.IDLE,
                remainingSeconds = duration * 60,
                totalSeconds = duration * 60
            )
            saveState()
        }
    }

    private fun saveState(endTime: Long = 0L) {
        viewModelScope.launch {
            saveTimerStateUseCase(
                PersistentTimerState(
                    sessionType = _uiState.value.sessionType,
                    timerState = _uiState.value.timerState,
                    remainingSeconds = _uiState.value.remainingSeconds,
                    totalSeconds = _uiState.value.totalSeconds,
                    endTime = endTime,
                    completedWorkSessions = _uiState.value.completedWorkSessions
                )
            )
        }
    }

    private fun startService(action: String, remainingSeconds: Int = -1) {
        val intent = Intent(context, TimerService::class.java).apply {
            this.action = action
            if (remainingSeconds != -1) {
                putExtra(TimerService.EXTRA_REMAINING_SECONDS, remainingSeconds)
            }
        }
        if (action == TimerService.ACTION_START || action == TimerService.ACTION_NEXT_STEP) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } else {
            context.startService(intent)
        }
    }
}
