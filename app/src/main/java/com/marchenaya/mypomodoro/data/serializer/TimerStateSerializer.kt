package com.marchenaya.mypomodoro.data.serializer

import androidx.datastore.core.Serializer
import com.marchenaya.mypomodoro.data.mapper.toPersistentTimerState
import com.marchenaya.mypomodoro.data.mapper.toPersistentTimerStateSerializable
import com.marchenaya.mypomodoro.data.model.PersistentTimerStateSerializable
import com.marchenaya.mypomodoro.domain.dispatcher.DispatcherProvider
import com.marchenaya.mypomodoro.domain.model.PersistentTimerState
import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.model.TimerState
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class TimerStateSerializer(
    private val dispatcherProvider: DispatcherProvider
) : Serializer<PersistentTimerState> {
    override val defaultValue: PersistentTimerState = PersistentTimerState(
        sessionType = SessionType.WORK,
        timerState = TimerState.IDLE,
        remainingSeconds = 25 * 60,
        totalSeconds = 25 * 60,
        endTime = 0L,
        completedWorkSessions = 0
    )

    override suspend fun readFrom(input: InputStream): PersistentTimerState {
        return try {
            Json.decodeFromString<PersistentTimerStateSerializable>(
                input.readBytes().decodeToString()
            ).toPersistentTimerState()
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: PersistentTimerState, output: OutputStream) {
        withContext(dispatcherProvider.io) {
            output.write(
                Json.encodeToString<PersistentTimerStateSerializable>(
                    t.toPersistentTimerStateSerializable()
                ).encodeToByteArray()
            )
        }
    }
}
