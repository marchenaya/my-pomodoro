package com.marchenaya.mypomodoro

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.marchenaya.mypomodoro.app.App
import com.marchenaya.mypomodoro.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "My Pomodoro",
        ) {
            App()
        }
    }
}
