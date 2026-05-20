package com.marchenaya.mypomodoro.data.datastore

import okio.Path

interface DataStorePathProvider {
    fun providePath(fileName: String): Path
}
