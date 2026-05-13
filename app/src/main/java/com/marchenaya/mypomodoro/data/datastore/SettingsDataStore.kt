package com.marchenaya.mypomodoro.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.marchenaya.mypomodoro.data.serializer.SettingsSerializer
import com.marchenaya.mypomodoro.domain.model.Settings

class SettingsDataStore(
    private val context: Context,
    private val settingsSerializer: SettingsSerializer
) {

    fun create(): DataStore<Settings> {
        return DataStoreFactory.create(
            serializer = settingsSerializer,
            produceFile = {
                context.dataStoreFile(SETTINGS_JSON)
            }
        )
    }

    companion object {
        private const val SETTINGS_JSON = "settings.json"
    }

}