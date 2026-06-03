package com.marchenaya.mypomodoro.data.service

import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.repository.TimerController
import com.marchenaya.mypomodoro.domain.usecase.GetTimerStateUseCase
import com.marchenaya.mypomodoro.presentation.util.DesktopNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mypomodoro.shared.generated.resources.Res
import mypomodoro.shared.generated.resources.long_break_complete_msg
import mypomodoro.shared.generated.resources.short_break_complete_msg
import mypomodoro.shared.generated.resources.work_complete_msg
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.getString

@OptIn(InternalResourceApi::class)
class TimerControllerImpl(
    private val commonTimerManager: CommonTimerManager,
    private val getTimerStateUseCase: GetTimerStateUseCase,
) : TimerController {

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun start(remainingSeconds: Int, endTime: Long, sessionType: SessionType) {
        commonTimerManager.start(
            initialRemainingSeconds = remainingSeconds,
            endTime = endTime,
            onFinished = {
                showNotification(sessionType)
            }
        )
    }

    override fun stop() {
        commonTimerManager.stop()
    }

    private fun showNotification(sessionType: SessionType) {
        scope.launch {
            val message = when (sessionType) {
                SessionType.WORK -> getString(Res.string.work_complete_msg)
                SessionType.SHORT_BREAK -> getString(Res.string.short_break_complete_msg)
                SessionType.LONG_BREAK -> getString(Res.string.long_break_complete_msg)
            }

            // Show Custom Compose Notification
            DesktopNotificationManager.show(message)
        }
    }

}
