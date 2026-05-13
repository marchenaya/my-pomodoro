package com.marchenaya.mypomodoro.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.marchenaya.mypomodoro.domain.model.Settings
import com.marchenaya.mypomodoro.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Settings> by dataStore(
    fileName = "settings.json",
    serializer = SettingsSerializer
)

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    override val workDurationFlow: Flow<Int> = context.settingsDataStore.data.map { settings ->
        settings.workDurationSeconds
    }

    override val shortBreakDurationFlow: Flow<Int> = context.settingsDataStore.data.map { settings ->
        settings.shortBreakDurationSeconds
    }

    override val longBreakDurationFlow: Flow<Int> = context.settingsDataStore.data.map { settings ->
        settings.longBreakDurationSeconds
    }

    override val sessionsBeforeLongBreakFlow: Flow<Int> =
        context.settingsDataStore.data.map { settings ->
            settings.sessionsBeforeLongBreak
        }

    override suspend fun updateWorkDuration(duration: Int) {
        context.settingsDataStore.updateData { currentSettings ->
            currentSettings.copy(workDurationSeconds = duration)
        }
    }

    override suspend fun updateShortBreakDuration(duration: Int) {
        context.settingsDataStore.updateData { currentSettings ->
            currentSettings.copy(shortBreakDurationSeconds = duration)
        }
    }

    override suspend fun updateLongBreakDuration(duration: Int) {
        context.settingsDataStore.updateData { currentSettings ->
            currentSettings.copy(longBreakDurationSeconds = duration)
        }
    }

    override suspend fun updateSessionsBeforeLongBreak(count: Int) {
        context.settingsDataStore.updateData { currentSettings ->
            currentSettings.copy(sessionsBeforeLongBreak = count)
        }
    }
}
