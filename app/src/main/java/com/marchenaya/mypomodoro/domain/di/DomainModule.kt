package com.marchenaya.mypomodoro.domain.di

import com.marchenaya.mypomodoro.domain.usecase.GetSettingsUseCase
import com.marchenaya.mypomodoro.domain.usecase.GetTimerStateUseCase
import com.marchenaya.mypomodoro.domain.usecase.SaveSettingsUseCase
import com.marchenaya.mypomodoro.domain.usecase.SaveTimerStateUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::GetSettingsUseCase)
    factoryOf(::SaveSettingsUseCase)
    factoryOf(::GetTimerStateUseCase)
    factoryOf(::SaveTimerStateUseCase)
}