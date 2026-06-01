package com.marchenaya.mypomodoro

import androidx.compose.ui.window.ComposeUIViewController
import com.marchenaya.mypomodoro.app.App
import com.marchenaya.mypomodoro.di.initKoin as initKoinCommon

fun MainViewController() = ComposeUIViewController { App() }

object KoinIos {
    fun initialize() {
        initKoinCommon { }
    }
}
