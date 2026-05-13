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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.marchenaya.mypomodoro.R
import com.marchenaya.mypomodoro.domain.model.SessionType
import com.marchenaya.mypomodoro.domain.model.TimerState
import com.marchenaya.mypomodoro.presentation.designsystem.ButtonSizeMedium
import com.marchenaya.mypomodoro.presentation.designsystem.IconSizeLarge
import com.marchenaya.mypomodoro.presentation.designsystem.IconSizeMedium
import com.marchenaya.mypomodoro.presentation.designsystem.MyPomodoroTheme
import com.marchenaya.mypomodoro.presentation.designsystem.PaddingHuge
import com.marchenaya.mypomodoro.presentation.designsystem.PaddingLarge
import com.marchenaya.mypomodoro.presentation.designsystem.PaddingMedium
import com.marchenaya.mypomodoro.presentation.designsystem.ProgressIndicatorSize
import com.marchenaya.mypomodoro.presentation.designsystem.ProgressIndicatorStrokeWidth
import com.marchenaya.mypomodoro.presentation.designsystem.TimerTextSize
import com.marchenaya.mypomodoro.presentation.util.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun TimerScreenRoot(
    viewModel: TimerViewModel = koinViewModel(),
    onSettingsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            TimerEvent.NavigateToSettings -> onSettingsClick()
        }
    }

    TimerScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun TimerScreen(
    uiState: TimerUiState,
    onAction: (TimerAction) -> Unit
) {
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
                    IconButton(onClick = { onAction(TimerAction.OnSettingsClick) }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
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
                    .padding(horizontal = PaddingMedium)
            ) {
                SessionType.entries.forEachIndexed { index, sessionType ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = SessionType.entries.size
                        ),
                        onClick = { onAction(TimerAction.SetSessionType(sessionType)) },
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

            Spacer(modifier = Modifier.height(PaddingHuge))

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = {
                        if (uiState.totalSeconds > 0) {
                            uiState.remainingSeconds.toFloat() / uiState.totalSeconds.toFloat()
                        } else 0f
                    },
                    modifier = Modifier.size(ProgressIndicatorSize),
                    color = when (uiState.sessionType) {
                        SessionType.WORK -> MaterialTheme.colorScheme.primary
                        SessionType.SHORT_BREAK -> MaterialTheme.colorScheme.secondary
                        SessionType.LONG_BREAK -> MaterialTheme.colorScheme.tertiary
                    },
                    strokeWidth = ProgressIndicatorStrokeWidth,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )

                Text(
                    text = formatTime(uiState.remainingSeconds),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = TimerTextSize,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(PaddingLarge))

            Text(
                text = if (uiState.sessionType == SessionType.WORK) {
                    pluralStringResource(
                        R.plurals.sessions_count_format,
                        uiState.completedWorkSessions,
                        uiState.completedWorkSessions
                    )
                } else "",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(PaddingLarge))

            Row(
                horizontalArrangement = Arrangement.spacedBy(PaddingLarge),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onAction(TimerAction.ResetTimer) },
                    modifier = Modifier.size(ButtonSizeMedium)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.reset),
                        modifier = Modifier.size(IconSizeMedium)
                    )
                }

                LargeFloatingActionButton(
                    onClick = {
                        if (uiState.timerState == TimerState.RUNNING) {
                            onAction(TimerAction.PauseTimer)
                        } else {
                            onAction(TimerAction.StartTimer)
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
                        modifier = Modifier.size(IconSizeLarge)
                    )
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val h = seconds / SECONDS_IN_HOUR
    val m = (seconds % SECONDS_IN_HOUR) / SECONDS_IN_MINUTE
    val s = seconds % SECONDS_IN_MINUTE
    return if (h > 0) {
        TIME_FORMAT_WITH_HOURS.format(h, m, s)
    } else {
        TIME_FORMAT_WITHOUT_HOURS.format(m, s)
    }
}

private const val SECONDS_IN_HOUR = 3600
private const val SECONDS_IN_MINUTE = 60

private const val TIME_FORMAT_WITH_HOURS = "%02d:%02d:%02d"
private const val TIME_FORMAT_WITHOUT_HOURS = "%02d:%02d"

@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    MyPomodoroTheme {
        Surface {
            TimerScreen(
                uiState = TimerUiState(),
                onAction = {}
            )
        }
    }
}
