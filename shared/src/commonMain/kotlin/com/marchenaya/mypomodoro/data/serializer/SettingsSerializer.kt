package com.marchenaya.mypomodoro.data.serializer

import androidx.datastore.core.okio.OkioSerializer
import com.marchenaya.mypomodoro.data.mapper.toSettings
import com.marchenaya.mypomodoro.data.mapper.toSettingsSerializable
import com.marchenaya.mypomodoro.data.model.SettingsSerializable
import com.marchenaya.mypomodoro.domain.dispatcher.DispatcherProvider
import com.marchenaya.mypomodoro.domain.model.Settings
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource

class SettingsSerializer(
    private val dispatcherProvider: DispatcherProvider
) : OkioSerializer<Settings> {
    override val defaultValue: Settings = Settings()

    override suspend fun readFrom(source: BufferedSource): Settings {
        return try {
            Json.decodeFromString<SettingsSerializable>(
                source.readUtf8()
            ).toSettings()
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: Settings, sink: BufferedSink) {
        withContext(dispatcherProvider.io) {
            sink.writeUtf8(
                Json.encodeToString<SettingsSerializable>(
                    t.toSettingsSerializable()
                )
            )
        }
    }
}
