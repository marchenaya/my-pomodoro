package com.marchenaya.mypomodoro.data

import androidx.datastore.core.Serializer
import com.marchenaya.mypomodoro.domain.repository.PersistentTimerState
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object TimerStateSerializer : Serializer<PersistentTimerState> {
    override val defaultValue: PersistentTimerState = PersistentTimerState(
        sessionType = com.marchenaya.mypomodoro.domain.model.SessionType.WORK,
        timerState = com.marchenaya.mypomodoro.domain.model.TimerState.IDLE,
        remainingSeconds = 25 * 60,
        totalSeconds = 25 * 60,
        endTime = 0L,
        completedWorkSessions = 0
    )

    override suspend fun readFrom(input: InputStream): PersistentTimerState {
        return try {
            Json.decodeFromString(
                deserializer = PersistentTimerState.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: PersistentTimerState, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = PersistentTimerState.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}
