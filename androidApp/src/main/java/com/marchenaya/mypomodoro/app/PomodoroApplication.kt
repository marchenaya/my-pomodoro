package com.marchenaya.mypomodoro.app

import android.app.Application
import com.marchenaya.mypomodoro.data.di.dataModule
import com.marchenaya.mypomodoro.domain.di.domainModule
import com.marchenaya.mypomodoro.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PomodoroApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@PomodoroApplication)
            modules(
                dataModule,
                domainModule,
                presentationModule
            )
        }
    }
}