package com.marchenaya.mypomodoro.data.service

import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.repository.TimerController
import com.marchenaya.mypomodoro.domain.usecase.GetTimerStateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mypomodoro.shared.generated.resources.Res
import mypomodoro.shared.generated.resources.app_name
import mypomodoro.shared.generated.resources.long_break_complete_msg
import mypomodoro.shared.generated.resources.short_break_complete_msg
import mypomodoro.shared.generated.resources.work_complete_msg
import org.jetbrains.compose.resources.getString
import java.awt.Image
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon

class TimerControllerImpl(
    private val commonTimerManager: CommonTimerManager,
    private val getTimerStateUseCase: GetTimerStateUseCase,
) : TimerController {

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun start(remainingSeconds: Int, endTime: Long) {
        commonTimerManager.start(
            initialRemainingSeconds = remainingSeconds,
            endTime = endTime,
            onFinished = {
                val state = getTimerStateUseCase().first()
                showNotification(state.sessionType)
            }
        )
    }

    override fun stop() {
        commonTimerManager.stop()
    }

    private fun showNotification(sessionType: SessionType) {
        if (!SystemTray.isSupported()) return

        scope.launch {
            val title = getString(Res.string.app_name)
            val message = when (sessionType) {
                SessionType.WORK -> getString(Res.string.work_complete_msg)
                SessionType.SHORT_BREAK -> getString(Res.string.short_break_complete_msg)
                SessionType.LONG_BREAK -> getString(Res.string.long_break_complete_msg)
            }

            val tray = SystemTray.getSystemTray()
            val image: Image = Toolkit.getDefaultToolkit().createImage("") // Empty image as icon
            val trayIcon = TrayIcon(image, title)
            trayIcon.isImageAutoSize = true

            try {
                tray.add(trayIcon)
                trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO)
                // Remove the icon after a delay to not clutter the tray
                launch {
                    kotlinx.coroutines.delay(5000)
                    tray.remove(trayIcon)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
