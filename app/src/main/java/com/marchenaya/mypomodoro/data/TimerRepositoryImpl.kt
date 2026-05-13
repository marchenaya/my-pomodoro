package com.marchenaya.mypomodoro.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.marchenaya.mypomodoro.domain.repository.PersistentTimerState
import com.marchenaya.mypomodoro.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow

private val Context.timerDataStore: DataStore<PersistentTimerState> by dataStore(
    fileName = "timer_state.json",
    serializer = TimerStateSerializer
)

class TimerRepositoryImpl(private val context: Context) : TimerRepository {

    override val timerStateFlow: Flow<PersistentTimerState> = context.timerDataStore.data

    override suspend fun saveTimerState(state: PersistentTimerState) {
        context.timerDataStore.updateData {
            state
        }
    }
}
