package com.marchenaya.mypomodoro.data.serializer

import androidx.datastore.core.Serializer
import com.marchenaya.mypomodoro.data.mapper.toSettings
import com.marchenaya.mypomodoro.data.mapper.toSettingsSerializable
import com.marchenaya.mypomodoro.data.model.SettingsSerializable
import com.marchenaya.mypomodoro.domain.dispatcher.DispatcherProvider
import com.marchenaya.mypomodoro.domain.model.Settings
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class SettingsSerializer(
    private val dispatcherProvider: DispatcherProvider
) : Serializer<Settings> {
    override val defaultValue: Settings = Settings()

    override suspend fun readFrom(input: InputStream): Settings {
        return try {
            Json.decodeFromString<SettingsSerializable>(
                input.readBytes().decodeToString()
            ).toSettings()
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: Settings, output: OutputStream) {
        withContext(dispatcherProvider.io) {
            output.write(
                Json.encodeToString<SettingsSerializable>(
                    t.toSettingsSerializable()
                ).encodeToByteArray()
            )
        }
    }
}
