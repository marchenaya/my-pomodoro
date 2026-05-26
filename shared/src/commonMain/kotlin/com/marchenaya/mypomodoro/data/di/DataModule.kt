package com.marchenaya.mypomodoro.data.di

import com.marchenaya.mypomodoro.data.datastore.SettingsDataStore
import com.marchenaya.mypomodoro.data.datastore.TimerStateDataStore
import com.marchenaya.mypomodoro.data.dispatcher.DefaultDispatcherProvider
import com.marchenaya.mypomodoro.data.repository.SettingsRepositoryImpl
import com.marchenaya.mypomodoro.data.repository.TimerRepositoryImpl
import com.marchenaya.mypomodoro.data.serializer.SettingsSerializer
import com.marchenaya.mypomodoro.data.serializer.TimerStateSerializer
import com.marchenaya.mypomodoro.data.service.CommonTimerManager
import com.marchenaya.mypomodoro.domain.dispatcher.DispatcherProvider
import com.marchenaya.mypomodoro.domain.repository.SettingsRepository
import com.marchenaya.mypomodoro.domain.repository.TimerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

private const val SETTINGS_DATA_STORE = "SettingsDataStore"
private const val TIMER_STATE_DATA_STORE = "TimerStateDataStore"

val dataModule = module {
    includes(platformDataModule)

    singleOf(::SettingsDataStore)
    singleOf(::SettingsSerializer)
    singleOf(::TimerStateDataStore)
    singleOf(::TimerStateSerializer)

    single(named(SETTINGS_DATA_STORE)) { get<SettingsDataStore>().create() }
    single(named(TIMER_STATE_DATA_STORE)) { get<TimerStateDataStore>().create() }

    single(named("AppScope")) { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    single { CommonTimerManager(get(), get(), get(named("AppScope"))) }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get(named(SETTINGS_DATA_STORE)))
    }
    single<TimerRepository> {
        TimerRepositoryImpl(get(named(TIMER_STATE_DATA_STORE)))
    }

    // TimerController is provided by platformDataModule

    singleOf(::DefaultDispatcherProvider) bind DispatcherProvider::class
}
