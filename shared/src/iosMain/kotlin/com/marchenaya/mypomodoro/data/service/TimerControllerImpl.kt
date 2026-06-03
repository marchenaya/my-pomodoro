package com.marchenaya.mypomodoro.data.service

import com.marchenaya.mypomodoro.domain.repository.TimerController
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

class TimerControllerImpl(
    private val commonTimerManager: CommonTimerManager
) : TimerController {

    override fun start(remainingSeconds: Int, endTime: Long) {
        scheduleNotification(remainingSeconds)
        commonTimerManager.start(remainingSeconds, endTime)
    }

    override fun stop() {
        commonTimerManager.stop()
        cancelNotification()
    }

    private fun scheduleNotification(seconds: Int) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val content = UNMutableNotificationContent().apply {
            setTitle("My Pomodoro")
            setBody("Time's up!")
            setSound(UNNotificationSound.defaultSound())
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

    private fun cancelNotification() {
        UNUserNotificationCenter.currentNotificationCenter()
            .removePendingNotificationRequestsWithIdentifiers(listOf(NOTIFICATION_ID))
    }

    companion object {
        private const val NOTIFICATION_ID = "timer_finished"
    }

}
