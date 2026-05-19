package com.marchenaya.mypomodoro.presentation.di

import com.marchenaya.mypomodoro.presentation.feature.settings.SettingsViewModel
import com.marchenaya.mypomodoro.presentation.feature.timer.TimerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::TimerViewModel)
    viewModelOf(::SettingsViewModel)
}
