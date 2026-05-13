package com.marchenaya.mypomodoro.data.repository

import androidx.datastore.core.DataStore
import com.marchenaya.mypomodoro.domain.model.Settings
import com.marchenaya.mypomodoro.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val settingsDataStore: DataStore<Settings>
) : SettingsRepository {

    override val workDurationFlow: Flow<Int> = settingsDataStore.data.map { settings ->
        settings.workDurationSeconds
    }

    override val shortBreakDurationFlow: Flow<Int> =
        settingsDataStore.data.map { settings ->
            settings.shortBreakDurationSeconds
        }

    override val longBreakDurationFlow: Flow<Int> = settingsDataStore.data.map { settings ->
        settings.longBreakDurationSeconds
    }

    override val sessionsBeforeLongBreakFlow: Flow<Int> =
        settingsDataStore.data.map { settings ->
            settings.sessionsBeforeLongBreak
        }

    override suspend fun updateWorkDuration(duration: Int) {
        settingsDataStore.updateData { currentSettings ->
            currentSettings.copy(workDurationSeconds = duration)
        }
    }

    override suspend fun updateShortBreakDuration(duration: Int) {
        settingsDataStore.updateData { currentSettings ->
            currentSettings.copy(shortBreakDurationSeconds = duration)
        }
    }

    override suspend fun updateLongBreakDuration(duration: Int) {
        settingsDataStore.updateData { currentSettings ->
            currentSettings.copy(longBreakDurationSeconds = duration)
        }
    }

    override suspend fun updateSessionsBeforeLongBreak(count: Int) {
        settingsDataStore.updateData { currentSettings ->
            currentSettings.copy(sessionsBeforeLongBreak = count)
        }
    }
}
