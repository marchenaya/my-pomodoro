package com.marchenaya.mypomodoro.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.marchenaya.mypomodoro.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    private object PreferencesKeys {
        val WORK_DURATION = intPreferencesKey("work_duration")
        val SHORT_BREAK_DURATION = intPreferencesKey("short_break_duration")
        val LONG_BREAK_DURATION = intPreferencesKey("long_break_duration")
        val SESSIONS_BEFORE_LONG_BREAK = intPreferencesKey("sessions_before_long_break")
    }

    override val workDurationFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.WORK_DURATION] ?: 25
    }

    override val shortBreakDurationFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SHORT_BREAK_DURATION] ?: 5
    }

    override val longBreakDurationFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LONG_BREAK_DURATION] ?: 15
    }

    override val sessionsBeforeLongBreakFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SESSIONS_BEFORE_LONG_BREAK] ?: 4
    }

    override suspend fun updateWorkDuration(duration: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WORK_DURATION] = duration
        }
    }

    override suspend fun updateShortBreakDuration(duration: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHORT_BREAK_DURATION] = duration
        }
    }

    override suspend fun updateLongBreakDuration(duration: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LONG_BREAK_DURATION] = duration
        }
    }

    override suspend fun updateSessionsBeforeLongBreak(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SESSIONS_BEFORE_LONG_BREAK] = count
        }
    }
}