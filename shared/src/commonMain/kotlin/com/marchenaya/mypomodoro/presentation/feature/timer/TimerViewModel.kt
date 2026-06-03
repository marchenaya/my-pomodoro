package com.marchenaya.mypomodoro.presentation.feature.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchenaya.mypomodoro.data.platform.currentTimeMillis
import com.marchenaya.mypomodoro.domain.model.PersistentTimerState
import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.model.TimerState
import com.marchenaya.mypomodoro.domain.usecase.GetSettingsUseCase
import com.marchenaya.mypomodoro.domain.usecase.GetTimerStatusUseCase
import com.marchenaya.mypomodoro.domain.usecase.SaveTimerStateUseCase
import com.marchenaya.mypomodoro.domain.usecase.StartTimerUseCase
import com.marchenaya.mypomodoro.domain.usecase.StopTimerUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TimerViewModel(
    private val getSettingsUseCase: GetSettingsUseCase,
    getTimerStatusUseCase: GetTimerStatusUseCase,
    private val saveTimerStateUseCase: SaveTimerStateUseCase,
    private val startTimerUseCase: StartTimerUseCase,
    private val stopTimerUseCase: StopTimerUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private val _events = Channel<TimerEvent>()
    val events = _events.receiveAsFlow()

    private var countdownJob: Job? = null

    init {
        getTimerStatusUseCase()
            .onEach { status ->
                val state = status.persistentTimerState
                val work = status.workDuration
                val short = status.shortBreakDuration
                val long = status.longBreakDuration

                val (processedRemaining, processedTotal) = if (state.timerState == TimerState.IDLE) {
                    val duration = when (state.sessionType) {
                        SessionType.WORK -> work
                        SessionType.SHORT_BREAK -> short
                        SessionType.LONG_BREAK -> long
                    }
                    duration to duration
                } else {
                    state.remainingSeconds to state.totalSeconds
                }

                _uiState.value = TimerUiState(
                    sessionType = state.sessionType,
                    timerState = state.timerState,
                    remainingSeconds = if (state.timerState == TimerState.RUNNING) {
                        ((state.endTime - currentTimeMillis()) / MILLIS_IN_SECOND).toInt()
                            .coerceAtLeast(0)
                    } else {
                        processedRemaining
                    },
                    totalSeconds = processedTotal,
                    completedWorkSessions = state.completedWorkSessions
                )

                if (state.timerState == TimerState.RUNNING) {
                    startUiCountdown(state.endTime)
                } else {
                    countdownJob?.cancel()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun startUiCountdown(endTime: Long) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var remaining = ((endTime - currentTimeMillis()) / MILLIS_IN_SECOND).toInt()
                .coerceAtLeast(0)
            while (remaining > 0) {
                _uiState.value = _uiState.value.copy(remainingSeconds = remaining)
                delay(UI_TICK_DELAY_MILLIS)
                remaining = ((endTime - currentTimeMillis()) / MILLIS_IN_SECOND).toInt()
                    .coerceAtLeast(0)
            }
            _uiState.value = _uiState.value.copy(remainingSeconds = 0)
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
        val endTime = currentTimeMillis() + (newRemaining * MILLIS_IN_SECOND)

        saveState(endTime)
        startTimerUseCase(newRemaining, endTime, _uiState.value.sessionType)
    }

    private fun pauseTimer() {
        _uiState.value = _uiState.value.copy(timerState = TimerState.PAUSED)
        saveState()
        stopTimerUseCase()
    }

    private fun resetTimer() {
        stopTimerUseCase()
        viewModelScope.launch {
            val duration = when (_uiState.value.sessionType) {
                SessionType.WORK -> getSettingsUseCase.workDuration.first()
                SessionType.SHORT_BREAK -> getSettingsUseCase.shortBreakDuration.first()
                SessionType.LONG_BREAK -> getSettingsUseCase.longBreakDuration.first()
            }
            _uiState.value = _uiState.value.copy(
                timerState = TimerState.IDLE,
                remainingSeconds = duration,
                totalSeconds = duration
            )
            saveState()
        }
    }

    private fun setSessionType(sessionType: SessionType) {
        stopTimerUseCase()
        viewModelScope.launch {
            val duration = when (sessionType) {
                SessionType.WORK -> getSettingsUseCase.workDuration.first()
                SessionType.SHORT_BREAK -> getSettingsUseCase.shortBreakDuration.first()
                SessionType.LONG_BREAK -> getSettingsUseCase.longBreakDuration.first()
            }
            _uiState.value = _uiState.value.copy(
                sessionType = sessionType,
                timerState = TimerState.IDLE,
                remainingSeconds = duration,
                totalSeconds = duration
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

    companion object {
        private const val MILLIS_IN_SECOND = 1000L
        private const val UI_TICK_DELAY_MILLIS = 500L
    }
}
