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
        val WORK_DURATION_SECONDS = intPreferencesKey("work_duration_seconds")
        val SHORT_BREAK_DURATION_SECONDS = intPreferencesKey("short_break_duration_seconds")
        val LONG_BREAK_DURATION_SECONDS = intPreferencesKey("long_break_duration_seconds")
        val SESSIONS_BEFORE_LONG_BREAK = intPreferencesKey("sessions_before_long_break")

        // Legacy keys for migration
        val WORK_DURATION_MIN = intPreferencesKey("work_duration")
        val SHORT_BREAK_DURATION_MIN = intPreferencesKey("short_break_duration")
        val LONG_BREAK_DURATION_MIN = intPreferencesKey("long_break_duration")
    }

    override val workDurationFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.WORK_DURATION_SECONDS]
            ?: (preferences[PreferencesKeys.WORK_DURATION_MIN]?.times(60) ?: (25 * 60))
    }

    override val shortBreakDurationFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SHORT_BREAK_DURATION_SECONDS]
            ?: (preferences[PreferencesKeys.SHORT_BREAK_DURATION_MIN]?.times(60) ?: (5 * 60))
    }

    override val longBreakDurationFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LONG_BREAK_DURATION_SECONDS]
            ?: (preferences[PreferencesKeys.LONG_BREAK_DURATION_MIN]?.times(60) ?: (15 * 60))
    }

    override val sessionsBeforeLongBreakFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SESSIONS_BEFORE_LONG_BREAK] ?: 4
    }

    override suspend fun updateWorkDuration(duration: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WORK_DURATION_SECONDS] = duration
        }
    }

    override suspend fun updateShortBreakDuration(duration: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHORT_BREAK_DURATION_SECONDS] = duration
        }
    }

    override suspend fun updateLongBreakDuration(duration: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LONG_BREAK_DURATION_SECONDS] = duration
        }
    }

    override suspend fun updateSessionsBeforeLongBreak(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SESSIONS_BEFORE_LONG_BREAK] = count
        }
    }
}