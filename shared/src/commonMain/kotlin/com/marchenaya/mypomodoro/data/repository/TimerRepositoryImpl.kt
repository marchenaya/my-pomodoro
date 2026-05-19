package com.marchenaya.mypomodoro.data.repository

import androidx.datastore.core.DataStore
import com.marchenaya.mypomodoro.domain.model.PersistentTimerState
import com.marchenaya.mypomodoro.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow

class TimerRepositoryImpl(
    private val timerDataStore: DataStore<PersistentTimerState>
) : TimerRepository {

    override val timerStateFlow: Flow<PersistentTimerState> = timerDataStore.data

    override suspend fun saveTimerState(state: PersistentTimerState) {
        timerDataStore.updateData {
            state
        }
    }

}
