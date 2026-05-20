package com.marchenaya.mypomodoro.data.datastore

import android.content.Context
import androidx.datastore.dataStoreFile
import okio.Path
import okio.Path.Companion.toPath

class AndroidDataStorePathProvider(
    private val context: Context
) : DataStorePathProvider {
    override fun providePath(fileName: String): Path {
        return context.dataStoreFile(fileName).absolutePath.toPath()
    }
}
