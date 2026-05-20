package com.marchenaya.mypomodoro.data.di

import com.marchenaya.mypomodoro.data.datastore.AndroidDataStorePathProvider
import com.marchenaya.mypomodoro.data.datastore.DataStorePathProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformDataModule: Module = module {
    singleOf(::AndroidDataStorePathProvider) bind DataStorePathProvider::class
}
