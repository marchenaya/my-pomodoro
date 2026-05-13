package com.marchenaya.mypomodoro.data.repository

import androidx.datastore.core.DataStore
import com.marchenaya.mypomodoro.data.service.TimerController
import com.marchenaya.mypomodoro.domain.model.PersistentTimerState
import com.marchenaya.mypomodoro.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow

class TimerRepositoryImpl(
    private val timerDataStore: DataStore<PersistentTimerState>,
    private val timerController: TimerController
) : TimerRepository {

    override val timerStateFlow: Flow<PersistentTimerState> = timerDataStore.data

    override suspend fun saveTimerState(state: PersistentTimerState) {
        timerDataStore.updateData {
            state
        }
    }

    override fun startTimer(remainingSeconds: Int, endTime: Long) {
        timerController.start(remainingSeconds, endTime)
    }

    override fun stopTimer() {
        timerController.stop()
    }
}
