package com.marchenaya.mypomodoro.presentation.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchenaya.mypomodoro.domain.usecase.GetSettingsUseCase
import com.marchenaya.mypomodoro.domain.usecase.SaveSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsViewModel(
    getSettingsUseCase: GetSettingsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        combine(
            getSettingsUseCase.workDuration,
            getSettingsUseCase.shortBreakDuration,
            getSettingsUseCase.longBreakDuration,
            getSettingsUseCase.sessionsBeforeLongBreak
        ) { work, short, long, sessions ->
            SettingsUiState(
                workDuration = work,
                shortBreakDuration = short,
                longBreakDuration = long,
                sessionsBeforeLongBreak = sessions
            )
        }.onEach { state ->
            _uiState.value = state
        }.launchIn(viewModelScope)
    }

    fun onAction(action: SettingsAction) {
        viewModelScope.launch {
            when (action) {
                is SettingsAction.UpdateWorkDuration -> {
                    saveSettingsUseCase.updateWorkDuration(action.durationSeconds)
                }

                is SettingsAction.UpdateShortBreakDuration -> {
                    saveSettingsUseCase.updateShortBreakDuration(action.durationSeconds)
                }

                is SettingsAction.UpdateLongBreakDuration -> {
                    saveSettingsUseCase.updateLongBreakDuration(action.durationSeconds)
                }

                is SettingsAction.UpdateSessionsBeforeLongBreak -> {
                    saveSettingsUseCase.updateSessionsBeforeLongBreak(action.count)
                }
            }
        }
    }

}
