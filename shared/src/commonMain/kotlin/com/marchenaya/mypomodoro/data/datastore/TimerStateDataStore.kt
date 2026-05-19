package com.marchenaya.mypomodoro.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.marchenaya.mypomodoro.data.serializer.TimerStateSerializer
import com.marchenaya.mypomodoro.domain.model.PersistentTimerState

class TimerStateDataStore(
    private val context: Context,
    private val timerStateSerializer: TimerStateSerializer
) {

    fun create(): DataStore<PersistentTimerState> {
        return DataStoreFactory.create(
            serializer = timerStateSerializer,
            produceFile = {
                context.dataStoreFile(TIMER_STATE_JSON)
            }
        )
    }

    companion object {
        private const val TIMER_STATE_JSON = "timer_state.json"
    }

}