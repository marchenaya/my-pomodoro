package com.marchenaya.mypomodoro.data.serializer

import androidx.datastore.core.okio.OkioSerializer
import com.marchenaya.mypomodoro.data.mapper.toPersistentTimerState
import com.marchenaya.mypomodoro.data.mapper.toPersistentTimerStateSerializable
import com.marchenaya.mypomodoro.data.model.PersistentTimerStateSerializable
import com.marchenaya.mypomodoro.domain.dispatcher.DispatcherProvider
import com.marchenaya.mypomodoro.domain.model.PersistentTimerState
import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.model.Settings
import com.marchenaya.mypomodoro.domain.model.TimerState
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource

class TimerStateSerializer(
    private val dispatcherProvider: DispatcherProvider
) : OkioSerializer<PersistentTimerState> {
    override val defaultValue: PersistentTimerState = PersistentTimerState(
        sessionType = SessionType.WORK,
        timerState = TimerState.IDLE,
        remainingSeconds = Settings.DEFAULT_WORK_DURATION,
        totalSeconds = Settings.DEFAULT_WORK_DURATION,
        endTime = DEFAULT_END_TIME,
        completedWorkSessions = DEFAULT_COMPLETED_SESSIONS
    )

    override suspend fun readFrom(source: BufferedSource): PersistentTimerState {
        return try {
            Json.decodeFromString<PersistentTimerStateSerializable>(
                source.readUtf8()
            ).toPersistentTimerState()
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: PersistentTimerState, sink: BufferedSink) {
        withContext(dispatcherProvider.io) {
            sink.writeUtf8(
                Json.encodeToString<PersistentTimerStateSerializable>(
                    t.toPersistentTimerStateSerializable()
                )
            )
        }
    }
}

private const val DEFAULT_END_TIME = 0L
private const val DEFAULT_COMPLETED_SESSIONS = 0
