package com.marchenaya.mypomodoro.data.datastore

import okio.Path
import okio.Path.Companion.toPath

class JvmDataStorePathProvider : DataStorePathProvider {
    override fun providePath(fileName: String): Path {
        return (System.getProperty("user.home") + "/$fileName").toPath()
    }
}
