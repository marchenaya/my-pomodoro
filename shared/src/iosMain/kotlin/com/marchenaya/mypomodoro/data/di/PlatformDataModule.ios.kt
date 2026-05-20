package com.marchenaya.mypomodoro.data.di

import com.marchenaya.mypomodoro.data.datastore.DataStorePathProvider
import com.marchenaya.mypomodoro.data.datastore.IosDataStorePathProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformDataModule: Module = module {
    singleOf(::IosDataStorePathProvider) bind DataStorePathProvider::class
}
