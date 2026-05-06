package com.marchenaya.mypomodoro.presentation.feature.timer

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.marchenaya.mypomodoro.R
import com.marchenaya.mypomodoro.domain.repository.SettingsRepository
import com.marchenaya.mypomodoro.domain.repository.PersistentTimerState
import com.marchenaya.mypomodoro.domain.repository.TimerRepository
import com.marchenaya.mypomodoro.domain.usecase.GetSettingsUseCase
import com.marchenaya.mypomodoro.domain.usecase.GetTimerStateUseCase
import com.marchenaya.mypomodoro.domain.usecase.SaveTimerStateUseCase
import com.marchenaya.mypomodoro.presentation.theme.MyPomodoroTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    onSettingsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermissionState = rememberPermissionState(
            Manifest.permission.POST_NOTIFICATIONS
        )
        LaunchedEffect(Unit) {
            if (!notificationPermissionState.status.isGranted) {
                notificationPermissionState.launchPermissionRequest()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SessionType.entries.forEachIndexed { index, sessionType ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = SessionType.entries.size
                        ),
                        onClick = { viewModel.setSessionType(sessionType, context) },
                        selected = uiState.sessionType == sessionType
                    ) {
                        Text(
                            when (sessionType) {
                                SessionType.WORK -> stringResource(R.string.work)
                                SessionType.SHORT_BREAK -> stringResource(R.string.short_break)
                                SessionType.LONG_BREAK -> stringResource(R.string.long_break)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = {
                        if (uiState.totalSeconds > 0) {
                            uiState.remainingSeconds.toFloat() / uiState.totalSeconds.toFloat()
                        } else 0f
                    },
                    modifier = Modifier.size(300.dp),
                    color = when (uiState.sessionType) {
                        SessionType.WORK -> MaterialTheme.colorScheme.primary
                        SessionType.SHORT_BREAK -> MaterialTheme.colorScheme.secondary
                        SessionType.LONG_BREAK -> MaterialTheme.colorScheme.tertiary
                    },
                    strokeWidth = 12.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )

                Text(
                    text = formatTime(uiState.remainingSeconds),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (uiState.sessionType == SessionType.WORK) {
                    stringResource(R.string.sessions_count_format, uiState.completedWorkSessions)
                } else "",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.resetTimer(context) },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.reset),
                        modifier = Modifier.size(32.dp)
                    )
                }

                LargeFloatingActionButton(
                    onClick = {
                        if (uiState.timerState == TimerState.RUNNING) {
                            viewModel.pauseTimer(context)
                        } else {
                            viewModel.startTimer(context)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(
                        imageVector = if (uiState.timerState == TimerState.RUNNING) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.PlayArrow
                        },
                        contentDescription = if (uiState.timerState == TimerState.RUNNING) {
                            stringResource(R.string.pause)
                        } else {
                            stringResource(R.string.start)
                        },
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(minutes, secs)
}

@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    val mockRepository = object : SettingsRepository {
        override val workDurationFlow: Flow<Int> = flowOf(25)
        override val shortBreakDurationFlow: Flow<Int> = flowOf(5)
        override val longBreakDurationFlow: Flow<Int> = flowOf(15)
        override val sessionsBeforeLongBreakFlow: Flow<Int> = flowOf(4)
        override suspend fun updateWorkDuration(duration: Int) {}
        override suspend fun updateShortBreakDuration(duration: Int) {}
        override suspend fun updateLongBreakDuration(duration: Int) {}
        override suspend fun updateSessionsBeforeLongBreak(count: Int) {}
    }
    val mockTimerRepository = object : TimerRepository {
        override val timerStateFlow: Flow<PersistentTimerState> = flowOf(
            PersistentTimerState(
                SessionType.WORK,
                TimerState.IDLE,
                25 * 60,
                25 * 60,
                0,
                0
            )
        )
        override suspend fun saveTimerState(state: PersistentTimerState) {}
    }
    val mockViewModel = TimerViewModel(
        GetSettingsUseCase(mockRepository),
        GetTimerStateUseCase(mockTimerRepository),
        SaveTimerStateUseCase(mockTimerRepository)
    )
    MyPomodoroTheme {
        Surface {
            TimerScreen(
                viewModel = mockViewModel,
                onSettingsClick = {}
            )
        }
    }
}