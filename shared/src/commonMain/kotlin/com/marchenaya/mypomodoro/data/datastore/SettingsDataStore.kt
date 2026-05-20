package com.marchenaya.mypomodoro.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import com.marchenaya.mypomodoro.data.serializer.SettingsSerializer
import com.marchenaya.mypomodoro.domain.model.Settings
import okio.FileSystem
import okio.SYSTEM

class SettingsDataStore(
    private val pathProvider: DataStorePathProvider,
    private val settingsSerializer: SettingsSerializer
) {

    fun create(): DataStore<Settings> {
        return DataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                serializer = settingsSerializer,
            ) {
                pathProvider.providePath(SETTINGS_JSON)
            }
        )
    }

    companion object {
        private const val SETTINGS_JSON = "settings.json"
    }

}