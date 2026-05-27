package com.marchenaya.mypomodoro.di

import com.marchenaya.mypomodoro.data.di.dataModule
import com.marchenaya.mypomodoro.domain.di.domainModule
import com.marchenaya.mypomodoro.presentation.di.presentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            dataModule,
            domainModule,
            presentationModule
        )
    }
