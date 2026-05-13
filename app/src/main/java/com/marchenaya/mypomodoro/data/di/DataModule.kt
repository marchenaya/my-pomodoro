package com.marchenaya.mypomodoro.data.di

import com.marchenaya.mypomodoro.data.datastore.SettingsDataStore
import com.marchenaya.mypomodoro.data.datastore.TimerStateDataStore
import com.marchenaya.mypomodoro.data.dispatcher.DefaultDispatcherProvider
import com.marchenaya.mypomodoro.data.repository.SettingsRepositoryImpl
import com.marchenaya.mypomodoro.data.repository.TimerRepositoryImpl
import com.marchenaya.mypomodoro.data.serializer.SettingsSerializer
import com.marchenaya.mypomodoro.data.serializer.TimerStateSerializer
import com.marchenaya.mypomodoro.domain.dispatcher.DispatcherProvider
import com.marchenaya.mypomodoro.domain.repository.SettingsRepository
import com.marchenaya.mypomodoro.domain.repository.TimerRepository
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::SettingsDataStore)
    singleOf(::SettingsSerializer)
    singleOf(::TimerStateDataStore)
    singleOf(::TimerStateSerializer)

    single(named("SettingsDataStore")) { get<SettingsDataStore>().create() }
    single(named("TimerStateDataStore")) { get<TimerStateDataStore>().create() }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get(named("SettingsDataStore")))
    }
    single<TimerRepository> {
        TimerRepositoryImpl(get(named("TimerStateDataStore")))
    }

    singleOf(::DefaultDispatcherProvider).bind<DispatcherProvider>()
}