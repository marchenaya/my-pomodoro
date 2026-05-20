package com.marchenaya.mypomodoro.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import com.marchenaya.mypomodoro.data.serializer.TimerStateSerializer
import com.marchenaya.mypomodoro.domain.model.PersistentTimerState
import okio.FileSystem
import okio.SYSTEM

class TimerStateDataStore(
    private val pathProvider: DataStorePathProvider,
    private val timerStateSerializer: TimerStateSerializer
) {

    fun create(): DataStore<PersistentTimerState> {
        return DataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                serializer = timerStateSerializer,
            ) {
                pathProvider.providePath(TIMER_STATE_JSON)
            }
        )
    }

    companion object {
        private const val TIMER_STATE_JSON = "timer_state.json"
    }

}