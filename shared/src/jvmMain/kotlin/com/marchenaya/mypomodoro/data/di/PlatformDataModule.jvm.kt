package com.marchenaya.mypomodoro.data.di

import com.marchenaya.mypomodoro.data.datastore.DataStorePathProvider
import com.marchenaya.mypomodoro.data.datastore.JvmDataStorePathProvider
import com.marchenaya.mypomodoro.data.service.TimerControllerImpl
import com.marchenaya.mypomodoro.domain.repository.TimerController
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformDataModule: Module = module {
    singleOf(::JvmDataStorePathProvider) bind DataStorePathProvider::class
    singleOf(::TimerControllerImpl) bind TimerController::class
}
