package com.marchenaya.mypomodoro.presentation.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object DesktopNotificationManager {
    private val _notificationState = MutableStateFlow<NotificationData?>(null)
    val notificationState = _notificationState.asStateFlow()

    fun show(message: String) {
        _notificationState.value = NotificationData(message)
    }

    fun dismiss() {
        _notificationState.value = null
    }

    data class NotificationData(val message: String)
}
