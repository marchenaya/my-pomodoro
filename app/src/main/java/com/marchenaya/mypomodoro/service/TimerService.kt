package com.marchenaya.mypomodoro.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.marchenaya.mypomodoro.MainActivity
import com.marchenaya.mypomodoro.R
import com.marchenaya.mypomodoro.domain.repository.PersistentTimerState
import com.marchenaya.mypomodoro.domain.usecase.GetSettingsUseCase
import com.marchenaya.mypomodoro.domain.usecase.GetTimerStateUseCase
import com.marchenaya.mypomodoro.domain.usecase.SaveTimerStateUseCase
import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.model.TimerState
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
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val remainingSeconds = intent.getIntExtra(EXTRA_REMAINING_SECONDS, -1)
                showInitialForegroundNotification()
                startTimer(remainingSeconds)
            }

            ACTION_STOP -> {
                stopTimer()
            }

            ACTION_NEXT_STEP -> {
                notificationManager.cancel(FINISHED_NOTIFICATION_ID)
                showInitialForegroundNotification()
                startNextStep()
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
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
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

    private fun startTimer(initialRemainingSeconds: Int = -1) {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            var state = getTimerStateUseCase().first()
            
            // Use passed remaining seconds if valid, otherwise use from state
            val startRemaining = if (initialRemainingSeconds != -1) {
                initialRemainingSeconds
            } else {
                state.remainingSeconds
            }

            // Force state to RUNNING if it's not already
            if (state.timerState != TimerState.RUNNING) {
                state = state.copy(timerState = TimerState.RUNNING, remainingSeconds = startRemaining)
                saveTimerStateUseCase(state)
            }

            notificationManager.notify(PROGRESS_NOTIFICATION_ID, createNotification(state))

            var remaining = startRemaining
            while (remaining > 0) {
                delay(1000)
                // Check state again to see if it was paused/stopped elsewhere
                val currentState = getTimerStateUseCase().first()
                if (currentState.timerState != TimerState.RUNNING) break
                
                remaining--
                val updatedState = currentState.copy(remainingSeconds = remaining)
                saveTimerStateUseCase(updatedState)
                notificationManager.notify(PROGRESS_NOTIFICATION_ID, createNotification(updatedState))
            }

            if (remaining <= 0) {
                onTimerFinished()
            } else {
                // If we broke out because state is no longer RUNNING, stop service
                stopTimer()
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
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

            val nextCompletedSessions = if (nextType == SessionType.LONG_BREAK || (currentType == SessionType.LONG_BREAK)) {
                0
            } else {
                completedSessions
            }

            val duration = when (nextType) {
                SessionType.WORK -> getSettingsUseCase.workDuration.first()
                SessionType.SHORT_BREAK -> getSettingsUseCase.shortBreakDuration.first()
                SessionType.LONG_BREAK -> getSettingsUseCase.longBreakDuration.first()
            }

            val newState = PersistentTimerState(
                sessionType = nextType,
                timerState = TimerState.RUNNING,
                remainingSeconds = duration * 60,
                totalSeconds = duration * 60,
                endTime = System.currentTimeMillis() + (duration * 60 * 1000L),
                completedWorkSessions = nextCompletedSessions
            )
            saveTimerStateUseCase(newState)
            startTimer()
        }
    }

    private suspend fun onTimerFinished() {
        val state = getTimerStateUseCase().first()
        val finishedState = state.copy(timerState = TimerState.IDLE, remainingSeconds = 0)
        saveTimerStateUseCase(finishedState)
        
        stopForeground(STOP_FOREGROUND_REMOVE)
        
        notificationManager.notify(FINISHED_NOTIFICATION_ID, createNotification(finishedState, isFinished = true))
        
        stopSelf()
    }

    private fun createNotification(state: PersistentTimerState, isFinished: Boolean = false): android.app.Notification {
        val channelId = if (isFinished) ALARM_CHANNEL_ID else PROGRESS_CHANNEL_ID
        val contentIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextStepIntent = Intent(this, TimerService::class.java).apply {
            action = ACTION_NEXT_STEP
        }
        val nextStepPendingIntent = PendingIntent.getService(
            this, 1, nextStepIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
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
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(if (isFinished) getString(R.string.app_name) else title)
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setOngoing(!isFinished)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(if (isFinished) NotificationCompat.CATEGORY_ALARM else NotificationCompat.CATEGORY_PROGRESS)
            .setAutoCancel(isFinished)
            .setOnlyAlertOnce(!isFinished)

        if (isFinished) {
            builder.setFullScreenIntent(pendingIntent, true)
        }

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
        
        const val EXTRA_REMAINING_SECONDS = "EXTRA_REMAINING_SECONDS"
    }
}
