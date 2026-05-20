package com.marchenaya.mypomodoro.data.datastore

import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

class IosDataStorePathProvider : DataStorePathProvider {
    @OptIn(ExperimentalForeignApi::class)
    override fun providePath(fileName: String): Path {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return (requireNotNull(documentDirectory).path + "/$fileName").toPath()
    }
}
