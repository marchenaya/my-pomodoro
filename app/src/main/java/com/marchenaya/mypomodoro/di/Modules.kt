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
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<TimerRepository> { TimerRepositoryImpl(get()) }
}

val domainModule = module {
    factory { GetSettingsUseCase(get()) }
    factory { SaveSettingsUseCase(get()) }
    factory { GetTimerStateUseCase(get()) }
    factory { SaveTimerStateUseCase(get()) }
}

val viewModelModule = module {
    viewModel { TimerViewModel(get(), get(), get()) }
}

val appModule = listOf(dataModule, domainModule, viewModelModule)