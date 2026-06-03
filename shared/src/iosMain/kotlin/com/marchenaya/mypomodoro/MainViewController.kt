package com.marchenaya.mypomodoro

import androidx.compose.ui.window.ComposeUIViewController
import com.marchenaya.mypomodoro.app.App
import com.marchenaya.mypomodoro.di.initKoin as initKoinCommon
import com.marchenaya.mypomodoro.domain.usecase.StartNextStepUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

fun MainViewController() = ComposeUIViewController { App() }

object KoinIos : KoinComponent {

    private var koinApp: KoinApplication? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun initialize() {
        koinApp = initKoinCommon { }
    }

    fun startNextStep(completionHandler: () -> Unit) {
        scope.launch {
            try {
                get<StartNextStepUseCase>().invoke()
            } finally {
                completionHandler()
            }
        }
    }

}
