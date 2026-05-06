package com.marchenaya.mypomodoro.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.marchenaya.mypomodoro.domain.repository.PersistentTimerState
import com.marchenaya.mypomodoro.domain.repository.TimerRepository
import com.marchenaya.mypomodoro.presentation.feature.timer.SessionType
import com.marchenaya.mypomodoro.presentation.feature.timer.TimerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.timerDataStore: DataStore<Preferences> by preferencesDataStore(name = "timer_state")

class TimerRepositoryImpl(private val context: Context) : TimerRepository {

    private object PreferencesKeys {
        val SESSION_TYPE = stringPreferencesKey("session_type")
        val TIMER_STATE = stringPreferencesKey("timer_state")
        val REMAINING_SECONDS = intPreferencesKey("remaining_seconds")
        val TOTAL_SECONDS = intPreferencesKey("total_seconds")
        val END_TIME = longPreferencesKey("end_time")
        val COMPLETED_WORK_SESSIONS = intPreferencesKey("completed_work_sessions")
    }

    override val timerStateFlow: Flow<PersistentTimerState> = context.timerDataStore.data.map { preferences ->
        PersistentTimerState(
            sessionType = SessionType.valueOf(preferences[PreferencesKeys.SESSION_TYPE] ?: SessionType.WORK.name),
            timerState = TimerState.valueOf(preferences[PreferencesKeys.TIMER_STATE] ?: TimerState.IDLE.name),
            remainingSeconds = preferences[PreferencesKeys.REMAINING_SECONDS] ?: (25 * 60),
            totalSeconds = preferences[PreferencesKeys.TOTAL_SECONDS] ?: (25 * 60),
            endTime = preferences[PreferencesKeys.END_TIME] ?: 0L,
            completedWorkSessions = preferences[PreferencesKeys.COMPLETED_WORK_SESSIONS] ?: 0
        )
    }

    override suspend fun saveTimerState(state: PersistentTimerState) {
        context.timerDataStore.edit { preferences ->
            preferences[PreferencesKeys.SESSION_TYPE] = state.sessionType.name
            preferences[PreferencesKeys.TIMER_STATE] = state.timerState.name
            preferences[PreferencesKeys.REMAINING_SECONDS] = state.remainingSeconds
            preferences[PreferencesKeys.TOTAL_SECONDS] = state.totalSeconds
            preferences[PreferencesKeys.END_TIME] = state.endTime
            preferences[PreferencesKeys.COMPLETED_WORK_SESSIONS] = state.completedWorkSessions
        }
    }
}
