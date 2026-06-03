package com.marchenaya.mypomodoro.data.service

import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.repository.TimerController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mypomodoro.shared.generated.resources.Res
import mypomodoro.shared.generated.resources.long_break_complete_msg
import mypomodoro.shared.generated.resources.short_break_complete_msg
import mypomodoro.shared.generated.resources.work_complete_msg
import org.jetbrains.compose.resources.getString
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

class TimerControllerImpl(
    private val commonTimerManager: CommonTimerManager
) : TimerController {

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun start(remainingSeconds: Int, endTime: Long, sessionType: SessionType) {
        scheduleNotification(remainingSeconds, sessionType)
        commonTimerManager.start(remainingSeconds, endTime)
    }

    override fun stop() {
        commonTimerManager.stop()
        cancelNotification()
    }

    private fun scheduleNotification(seconds: Int, sessionType: SessionType) {
        val center = UNUserNotificationCenter.currentNotificationCenter()

        scope.launch {
            val message = when (sessionType) {
                SessionType.WORK -> getString(Res.string.work_complete_msg)
                SessionType.SHORT_BREAK -> getString(Res.string.short_break_complete_msg)
                SessionType.LONG_BREAK -> getString(Res.string.long_break_complete_msg)
            }

            val content = UNMutableNotificationContent().apply {
                setTitle("My Pomodoro")
                setBody(message)
                setSound(UNNotificationSound.defaultSound())
                setCategoryIdentifier(TIMER_CATEGORY_ID)
            }

            val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
                seconds.toDouble().coerceAtLeast(0.1),
                repeats = false
            )

            val request = UNNotificationRequest.requestWithIdentifier(
                NOTIFICATION_ID,
                content,
                trigger
            )

            center.addNotificationRequest(request) { error ->
                if (error != null) {
                    println("Error scheduling notification: $error")
                }
            }
        }
    }

    private fun cancelNotification() {
        UNUserNotificationCenter.currentNotificationCenter()
            .removePendingNotificationRequestsWithIdentifiers(listOf(NOTIFICATION_ID))
    }

    companion object {
        private const val NOTIFICATION_ID = "timer_finished"
        private const val TIMER_CATEGORY_ID = "timer_category"
    }

}
