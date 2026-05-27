package com.marchenaya.mypomodoro.app

import android.app.Application
import com.marchenaya.mypomodoro.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class PomodoroApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger()
            androidContext(this@PomodoroApplication)
        }
    }
}