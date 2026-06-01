package com.marchenaya.mypomodoro

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.marchenaya.mypomodoro.app.App
import com.marchenaya.mypomodoro.di.initKoin
import com.marchenaya.mypomodoro.domain.usecase.StartNextStepUseCase
import com.marchenaya.mypomodoro.presentation.components.NotificationPopup
import com.marchenaya.mypomodoro.presentation.util.DesktopNotificationManager
import kotlinx.coroutines.launch
import mypomodoro.shared.generated.resources.Res
import mypomodoro.shared.generated.resources.ic_launcher
import org.jetbrains.compose.resources.painterResource
import org.koin.java.KoinJavaComponent.inject

fun main() {
    initKoin()
    application {
        val startNextStepUseCase: StartNextStepUseCase by inject(StartNextStepUseCase::class.java)
        val scope = rememberCoroutineScope()
        val notificationState by DesktopNotificationManager.notificationState.collectAsState()

        Window(
            onCloseRequest = ::exitApplication,
            title = "My Pomodoro",
            icon = painterResource(Res.drawable.ic_launcher)
        ) {
            App()
        }

        notificationState?.let { data ->
            NotificationPopup(
                message = data.message,
                onNextStepClick = {
                    scope.launch {
                        startNextStepUseCase()
                    }
                },
                onClose = { DesktopNotificationManager.dismiss() }
            )
        }
    }
}
