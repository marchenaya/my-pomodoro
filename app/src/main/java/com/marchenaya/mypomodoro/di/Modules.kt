package com.marchenaya.mypomodoro.di

import com.marchenaya.mypomodoro.data.SettingsRepositoryImpl
import com.marchenaya.mypomodoro.data.TimerRepositoryImpl
import com.marchenaya.mypomodoro.domain.repository.SettingsRepository
import com.marchenaya.mypomodoro.domain.repository.TimerRepository
import com.marchenaya.mypomodoro.domain.usecase.GetSettingsUseCase
import com.marchenaya.mypomodoro.domain.usecase.GetTimerStateUseCase
import com.marchenaya.mypomodoro.domain.usecase.SaveSettingsUseCase
import com.marchenaya.mypomodoro.domain.usecase.SaveTimerStateUseCase
import com.marchenaya.mypomodoro.presentation.feature.timer.TimerViewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.factory
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

val appModule = module {
    single<SettingsRepositoryImpl>() bind SettingsRepository::class
    single<TimerRepositoryImpl>() bind TimerRepository::class

    factory<GetSettingsUseCase>()
    factory<SaveSettingsUseCase>()
    factory<GetTimerStateUseCase>()
    factory<SaveTimerStateUseCase>()

    viewModel<TimerViewModel>()
}