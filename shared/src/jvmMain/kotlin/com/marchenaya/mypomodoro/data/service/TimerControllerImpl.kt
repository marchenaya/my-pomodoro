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
import mypomodoro.shared.generated.resources.app_name
import mypomodoro.shared.generated.resources.long_break_complete_msg
import mypomodoro.shared.generated.resources.short_break_complete_msg
import mypomodoro.shared.generated.resources.work_complete_msg
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.getString
import java.awt.Image
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import javax.imageio.ImageIO

@OptIn(InternalResourceApi::class)
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

            // Show Custom Compose Notification
            DesktopNotificationManager.show(message)

            // Also show Tray notification (native) with icon
            val tray = SystemTray.getSystemTray()
            val image: Image? = try {
                // Try to load the icon from resources using the standard path for Compose Multiplatform
                val iconStream =
                    javaClass.classLoader.getResourceAsStream("composeResources/mypomodoro.shared.generated.resources/drawable/ic_launcher.png")
                if (iconStream != null) {
                    ImageIO.read(iconStream)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }

            val trayIcon = TrayIcon(image ?: Toolkit.getDefaultToolkit().createImage(""), title)
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
