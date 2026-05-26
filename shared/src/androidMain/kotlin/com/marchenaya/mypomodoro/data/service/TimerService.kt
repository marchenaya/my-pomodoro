package com.marchenaya.mypomodoro.data.service

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
import com.marchenaya.mypomodoro.data.platform.currentTimeMillis
import com.marchenaya.mypomodoro.domain.model.PersistentTimerState
import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.model.TimerState
import com.marchenaya.mypomodoro.domain.usecase.GetSettingsUseCase
import com.marchenaya.mypomodoro.domain.usecase.GetTimerStateUseCase
import com.marchenaya.mypomodoro.domain.usecase.SaveTimerStateUseCase
import com.marchenaya.mypomodoro.shared.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mypomodoro.shared.generated.resources.Res
import mypomodoro.shared.generated.resources.app_name
import mypomodoro.shared.generated.resources.long_break
import mypomodoro.shared.generated.resources.long_break_complete_msg
import mypomodoro.shared.generated.resources.next_step
import mypomodoro.shared.generated.resources.remaining_time_hours_format
import mypomodoro.shared.generated.resources.remaining_time_minutes_format
import mypomodoro.shared.generated.resources.remaining_time_seconds_format
import mypomodoro.shared.generated.resources.short_break
import mypomodoro.shared.generated.resources.short_break_complete_msg
import mypomodoro.shared.generated.resources.timer
import mypomodoro.shared.generated.resources.timer_alerts_channel_description
import mypomodoro.shared.generated.resources.timer_alerts_channel_name
import mypomodoro.shared.generated.resources.timer_progress_channel_description
import mypomodoro.shared.generated.resources.timer_progress_channel_name
import mypomodoro.shared.generated.resources.work
import mypomodoro.shared.generated.resources.work_complete_msg
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString
import org.koin.android.ext.android.inject
import android.app.Notification as AndroidNotification

class TimerService : Service() {

    private val getSettingsUseCase: GetSettingsUseCase by inject()
    private val getTimerStateUseCase: GetTimerStateUseCase by inject()
    private val saveTimerStateUseCase: SaveTimerStateUseCase by inject()
    private val commonTimerManager: CommonTimerManager by inject()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

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
                val remainingSeconds = intent.getIntExtra(EXTRA_REMAINING_SECONDS, INVALID_TIME)
                val endTime = intent.getLongExtra(EXTRA_END_TIME, INVALID_TIME_LONG)
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    showInitialForegroundNotification()
                }
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
        serviceScope.launch {
            val notification: AndroidNotification =
                NotificationCompat.Builder(this@TimerService, PROGRESS_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(Res.string.timer))
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
    }

    private fun startTimer(initialRemainingSeconds: Int = -1, initialEndTime: Long = -1L) {
        serviceScope.launch {
            val state = getTimerStateUseCase().first()

            // Use passed remaining seconds if valid, otherwise use from state
            val startRemaining = if (initialRemainingSeconds != INVALID_TIME) {
                initialRemainingSeconds
            } else {
                state.remainingSeconds
            }

            val endTime = if (initialEndTime != INVALID_TIME_LONG) {
                initialEndTime
            } else {
                currentTimeMillis() + (startRemaining * MILLIS_IN_SECOND)
            }

            scheduleCompletionAlarm(endTime)

            commonTimerManager.start(
                initialRemainingSeconds = startRemaining,
                endTime = endTime,
                onTick = { remaining ->
                    val currentState = getTimerStateUseCase().first()
                    notificationManager.notify(
                        PROGRESS_NOTIFICATION_ID,
                        createNotification(currentState.copy(remainingSeconds = remaining))
                    )
                },
                onFinished = {
                    onTimerFinished()
                }
            )
        }
    }

    private fun scheduleCompletionAlarm(endTimeMillis: Long) {
        val intent = Intent(this, TimerReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
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
            this,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun stopTimer() {
        commonTimerManager.stop()
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

            val endTime = currentTimeMillis() + (duration * MILLIS_IN_SECOND)
            val newState = PersistentTimerState(
                sessionType = nextType,
                timerState = TimerState.RUNNING,
                remainingSeconds = duration,
                totalSeconds = duration,
                endTime = endTime,
                completedWorkSessions = nextCompletedSessions
            )
            saveTimerStateUseCase(newState)
            startTimer(duration, endTime)
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

            commonTimerManager.stop()
            stopSelf()
        }
    }

    private suspend fun createNotification(
        state: PersistentTimerState,
        isFinished: Boolean = false
    ): AndroidNotification {
        val channelId = if (isFinished) ALARM_CHANNEL_ID else PROGRESS_CHANNEL_ID
        // We use a generic way to find MainActivity if possible, or use the hardcoded one if it's the only way
        val contentIntent = Intent().apply {
            setClassName(packageName, "com.marchenaya.mypomodoro.app.MainActivity")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            MAIN_ACTIVITY_REQUEST_CODE,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextStepIntent = Intent(this, TimerService::class.java).apply {
            action = ACTION_NEXT_STEP
        }
        val nextStepPendingIntent = PendingIntent.getService(
            this,
            NEXT_STEP_REQUEST_CODE,
            nextStepIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when (state.sessionType) {
            SessionType.WORK -> getString(Res.string.work)
            SessionType.SHORT_BREAK -> getString(Res.string.short_break)
            SessionType.LONG_BREAK -> getString(Res.string.long_break)
        }

        val contentText = if (isFinished) {
            when (state.sessionType) {
                SessionType.WORK -> getString(Res.string.work_complete_msg)
                SessionType.SHORT_BREAK -> getString(Res.string.short_break_complete_msg)
                SessionType.LONG_BREAK -> getString(Res.string.long_break_complete_msg)
            }
        } else {
            val totalSeconds = state.remainingSeconds
            val hours = totalSeconds / SECONDS_IN_HOUR
            val minutes = (totalSeconds % SECONDS_IN_HOUR) / SECONDS_IN_MINUTE
            val seconds = totalSeconds % SECONDS_IN_MINUTE

            when {
                hours > 0 -> {
                    getPluralString(
                        Res.plurals.remaining_time_hours_format,
                        hours,
                        hours,
                        minutes,
                        seconds
                    )
                }

                minutes > 0 -> {
                    getPluralString(
                        Res.plurals.remaining_time_minutes_format,
                        minutes,
                        minutes,
                        seconds
                    )
                }

                else -> {
                    getPluralString(
                        Res.plurals.remaining_time_seconds_format,
                        seconds,
                        seconds
                    )
                }
            }
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(if (isFinished) getString(Res.string.app_name) else title)
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setOngoing(!isFinished)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(if (isFinished) NotificationCompat.CATEGORY_ALARM else NotificationCompat.CATEGORY_PROGRESS)
            .setAutoCancel(isFinished)
            .setOnlyAlertOnce(!isFinished)

        if (isFinished) {
            builder.setDefaults(NotificationCompat.DEFAULT_ALL)
        }

        builder.addAction(
            android.R.drawable.ic_media_next,
            getString(Res.string.next_step),
            nextStepPendingIntent
        )

        return builder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val progressChannel = NotificationChannel(
                PROGRESS_CHANNEL_ID,
                runBlocking { getString(Res.string.timer_progress_channel_name) },
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description =
                    runBlocking { getString(Res.string.timer_progress_channel_description) }
                setShowBadge(false)
            }

            val alarmChannel = NotificationChannel(
                ALARM_CHANNEL_ID,
                runBlocking { getString(Res.string.timer_alerts_channel_name) },
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = runBlocking { getString(Res.string.timer_alerts_channel_description) }
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

        private const val MILLIS_IN_SECOND = 1000L
        private const val SECONDS_IN_MINUTE = 60
        private const val SECONDS_IN_HOUR = 3600
        private const val MAIN_ACTIVITY_REQUEST_CODE = 0
        private const val NEXT_STEP_REQUEST_CODE = 1
        private const val ALARM_REQUEST_CODE = 0

        const val INVALID_TIME = -1
        const val INVALID_TIME_LONG = -1L
    }
}
