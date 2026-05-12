package com.marchenaya.mypomodoro.service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.marchenaya.mypomodoro.MainActivity
import com.marchenaya.mypomodoro.R
import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.model.TimerState
import com.marchenaya.mypomodoro.domain.repository.PersistentTimerState
import com.marchenaya.mypomodoro.domain.usecase.GetSettingsUseCase
import com.marchenaya.mypomodoro.domain.usecase.GetTimerStateUseCase
import com.marchenaya.mypomodoro.domain.usecase.SaveTimerStateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class TimerService : Service() {

    private val getSettingsUseCase: GetSettingsUseCase by inject()
    private val getTimerStateUseCase: GetTimerStateUseCase by inject()
    private val saveTimerStateUseCase: SaveTimerStateUseCase by inject()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var timerJob: Job? = null

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private val alarmManager by lazy {
        getSystemService(ALARM_SERVICE) as AlarmManager
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val remainingSeconds = intent.getIntExtra(EXTRA_REMAINING_SECONDS, -1)
                val endTime = intent.getLongExtra(EXTRA_END_TIME, -1L)
                showInitialForegroundNotification()
                startTimer(remainingSeconds, endTime)
            }

            ACTION_STOP -> {
                stopTimer()
            }

            ACTION_NEXT_STEP -> {
                notificationManager.cancel(FINISHED_NOTIFICATION_ID)
                showInitialForegroundNotification()
                startNextStep()
            }

            ACTION_FINISHED -> {
                serviceScope.launch {
                    onTimerFinished()
                }
            }

            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    showInitialForegroundNotification()
                }
            }
        }
        return START_STICKY
    }

    private fun showInitialForegroundNotification() {
        val notification = NotificationCompat.Builder(this, PROGRESS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.timer))
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                PROGRESS_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(PROGRESS_NOTIFICATION_ID, notification)
        }
    }

    private fun startTimer(initialRemainingSeconds: Int = -1, initialEndTime: Long = -1L) {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            var state = getTimerStateUseCase().first()

            // Use passed remaining seconds if valid, otherwise use from state
            val startRemaining = if (initialRemainingSeconds != -1) {
                initialRemainingSeconds
            } else {
                state.remainingSeconds
            }

            val endTime = if (initialEndTime != -1L) {
                initialEndTime
            } else {
                System.currentTimeMillis() + (startRemaining * 1000L)
            }

            // Force state to RUNNING if it's not already
            if (state.timerState != TimerState.RUNNING || state.endTime != endTime) {
                state = state.copy(
                    timerState = TimerState.RUNNING,
                    remainingSeconds = startRemaining,
                    endTime = endTime
                )
                saveTimerStateUseCase(state)
            }

            scheduleCompletionAlarm(endTime)
            notificationManager.notify(PROGRESS_NOTIFICATION_ID, createNotification(state))

            var lastSavedRemaining = startRemaining
            while (true) {
                val currentTime = System.currentTimeMillis()
                val remaining = ((endTime - currentTime) / 1000).toInt().coerceAtLeast(0)

                // Check state again to see if it was paused/stopped elsewhere
                val currentState = getTimerStateUseCase().first()
                if (currentState.timerState != TimerState.RUNNING) break

                val updatedState = currentState.copy(remainingSeconds = remaining)
                notificationManager.notify(
                    PROGRESS_NOTIFICATION_ID,
                    createNotification(updatedState)
                )

                // Save to DataStore occasionally to keep UI in sync if service restarts
                if (lastSavedRemaining - remaining >= 5 || remaining == 0) {
                    saveTimerStateUseCase(updatedState)
                    lastSavedRemaining = remaining
                }

                if (remaining <= 0) break
                delay(1000)
            }

            // We don't call onTimerFinished here because the AlarmManager will trigger it via ACTION_FINISHED
            // This ensures it fires even if the service loop is suspended.
            // However, if we are still running, we can stop the service now.
            if (((endTime - System.currentTimeMillis()) / 1000).toInt() <= 0) {
                // If we are already finished, let ACTION_FINISHED handle it or trigger it now if missed
                onTimerFinished()
            }
        }
    }

    private fun scheduleCompletionAlarm(endTimeMillis: Long) {
        val intent = Intent(this, TimerReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Fallback to inexact or just rely on the foreground service if permission not granted
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                endTimeMillis,
                pendingIntent
            )
            return
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            endTimeMillis,
            pendingIntent
        )
    }

    private fun cancelCompletionAlarm() {
        val intent = Intent(this, TimerReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun stopTimer() {
        timerJob?.cancel()
        cancelCompletionAlarm()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startNextStep() {
        serviceScope.launch {
            val state = getTimerStateUseCase().first()
            val currentType = state.sessionType
            val completedSessions = if (currentType == SessionType.WORK) {
                state.completedWorkSessions + 1
            } else {
                state.completedWorkSessions
            }

            val sessionsBeforeLongBreak = getSettingsUseCase.sessionsBeforeLongBreak.first()

            val nextType = when (currentType) {
                SessionType.WORK -> {
                    if (completedSessions >= sessionsBeforeLongBreak) {
                        SessionType.LONG_BREAK
                    } else {
                        SessionType.SHORT_BREAK
                    }
                }

                SessionType.SHORT_BREAK, SessionType.LONG_BREAK -> SessionType.WORK
            }

            val nextCompletedSessions =
                if (nextType == SessionType.LONG_BREAK || (currentType == SessionType.LONG_BREAK)) {
                    0
                } else {
                    completedSessions
                }

            val duration = when (nextType) {
                SessionType.WORK -> getSettingsUseCase.workDuration.first()
                SessionType.SHORT_BREAK -> getSettingsUseCase.shortBreakDuration.first()
                SessionType.LONG_BREAK -> getSettingsUseCase.longBreakDuration.first()
            }

            val durationSeconds = duration * 60
            val endTime = System.currentTimeMillis() + (durationSeconds * 1000L)
            val newState = PersistentTimerState(
                sessionType = nextType,
                timerState = TimerState.RUNNING,
                remainingSeconds = durationSeconds,
                totalSeconds = durationSeconds,
                endTime = endTime,
                completedWorkSessions = nextCompletedSessions
            )
            saveTimerStateUseCase(newState)
            startTimer(durationSeconds, endTime)
        }
    }

    private suspend fun onTimerFinished() {
        val state = getTimerStateUseCase().first()
        // Only proceed if it was actually running and time is up (avoid double triggers)
        if (state.timerState == TimerState.RUNNING || state.remainingSeconds > 0) {
            val finishedState = state.copy(timerState = TimerState.IDLE, remainingSeconds = 0)
            saveTimerStateUseCase(finishedState)

            stopForeground(STOP_FOREGROUND_REMOVE)

            notificationManager.notify(
                FINISHED_NOTIFICATION_ID,
                createNotification(finishedState, isFinished = true)
            )

            timerJob?.cancel()
            stopSelf()
        }
    }

    private fun createNotification(
        state: PersistentTimerState,
        isFinished: Boolean = false
    ): android.app.Notification {
        val channelId = if (isFinished) ALARM_CHANNEL_ID else PROGRESS_CHANNEL_ID
        val contentIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextStepIntent = Intent(this, TimerService::class.java).apply {
            action = ACTION_NEXT_STEP
        }
        val nextStepPendingIntent = PendingIntent.getService(
            this,
            1,
            nextStepIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when (state.sessionType) {
            SessionType.WORK -> getString(R.string.work)
            SessionType.SHORT_BREAK -> getString(R.string.short_break)
            SessionType.LONG_BREAK -> getString(R.string.long_break)
        }

        val contentText = if (isFinished) {
            when (state.sessionType) {
                SessionType.WORK -> getString(R.string.work_complete_msg)
                SessionType.SHORT_BREAK -> getString(R.string.short_break_complete_msg)
                SessionType.LONG_BREAK -> getString(R.string.long_break_complete_msg)
            }
        } else {
            val minutes = state.remainingSeconds / 60
            val seconds = state.remainingSeconds % 60
            getString(R.string.remaining_time_format, minutes, seconds)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(if (isFinished) getString(R.string.app_name) else title)
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setOngoing(!isFinished)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(if (isFinished) NotificationCompat.CATEGORY_ALARM else NotificationCompat.CATEGORY_PROGRESS)
            .setAutoCancel(isFinished)
            .setOnlyAlertOnce(!isFinished)

        builder.addAction(
            android.R.drawable.ic_media_next,
            getString(R.string.next_step),
            nextStepPendingIntent
        )

        return builder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val progressChannel = NotificationChannel(
                PROGRESS_CHANNEL_ID,
                getString(R.string.timer_progress_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.timer_progress_channel_description)
                setShowBadge(false)
            }

            val alarmChannel = NotificationChannel(
                ALARM_CHANNEL_ID,
                getString(R.string.timer_alerts_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.timer_alerts_channel_description)
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(progressChannel)
            notificationManager.createNotificationChannel(alarmChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val PROGRESS_NOTIFICATION_ID = 1
        const val FINISHED_NOTIFICATION_ID = 2

        const val PROGRESS_CHANNEL_ID = "timer_progress_channel"
        const val ALARM_CHANNEL_ID = "timer_alarm_channel"

        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_NEXT_STEP = "ACTION_NEXT_STEP"
        const val ACTION_FINISHED = "ACTION_FINISHED"

        const val EXTRA_REMAINING_SECONDS = "EXTRA_REMAINING_SECONDS"
        const val EXTRA_END_TIME = "EXTRA_END_TIME"
    }
}
