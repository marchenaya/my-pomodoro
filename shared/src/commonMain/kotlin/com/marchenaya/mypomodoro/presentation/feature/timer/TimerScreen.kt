package com.marchenaya.mypomodoro.presentation.feature.timer

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
import com.marchenaya.mypomodoro.presentation.util.koinViewModel
import mypomodoro.shared.generated.resources.Res
import mypomodoro.shared.generated.resources.app_name
import mypomodoro.shared.generated.resources.long_break
import mypomodoro.shared.generated.resources.pause
import mypomodoro.shared.generated.resources.reset
import mypomodoro.shared.generated.resources.sessions_count_format
import mypomodoro.shared.generated.resources.settings
import mypomodoro.shared.generated.resources.short_break
import mypomodoro.shared.generated.resources.start
import mypomodoro.shared.generated.resources.work
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TimerScreenRoot(
    viewModel: TimerViewModel = koinViewModel(),
    onSettingsClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            TimerEvent.NavigateToSettings -> onSettingsClick()
        }
    }

    TimerScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun TimerScreen(
    uiState: TimerUiState,
    onAction: (TimerAction) -> Unit
) {
    Scaffold(
        topBar = {
            TimerTopBar(onSettingsClick = { onAction(TimerAction.OnSettingsClick) })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SessionTypeSelector(
                selectedSessionType = uiState.sessionType,
                onSessionTypeSelected = { onAction(TimerAction.SetSessionType(it)) }
            )

            Spacer(modifier = Modifier.height(PaddingHuge))

            TimerProgressIndicator(
                remainingSeconds = uiState.remainingSeconds,
                totalSeconds = uiState.totalSeconds,
                sessionType = uiState.sessionType
            )

            Spacer(modifier = Modifier.height(PaddingLarge))

            CompletedSessionsText(
                sessionType = uiState.sessionType,
                completedWorkSessions = uiState.completedWorkSessions
            )

            Spacer(modifier = Modifier.height(PaddingLarge))

            TimerControls(
                timerState = uiState.timerState,
                onStartClick = { onAction(TimerAction.StartTimer) },
                onPauseClick = { onAction(TimerAction.PauseTimer) },
                onResetClick = { onAction(TimerAction.ResetTimer) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimerTopBar(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(Res.string.app_name)) },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = stringResource(Res.string.settings)
                )
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionTypeSelector(
    selectedSessionType: SessionType,
    onSessionTypeSelected: (SessionType) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingMedium)
    ) {
        SessionType.entries.forEachIndexed { index, sessionType ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = SessionType.entries.size
                ),
                onClick = { onSessionTypeSelected(sessionType) },
                selected = selectedSessionType == sessionType
            ) {
                Text(
                    when (sessionType) {
                        SessionType.WORK -> stringResource(Res.string.work)
                        SessionType.SHORT_BREAK -> stringResource(Res.string.short_break)
                        SessionType.LONG_BREAK -> stringResource(Res.string.long_break)
                    }
                )
            }
        }
    }
}

@Composable
private fun TimerProgressIndicator(
    remainingSeconds: Int,
    totalSeconds: Int,
    sessionType: SessionType,
    modifier: Modifier = Modifier
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        CircularProgressIndicator(
            progress = {
                if (totalSeconds > 0) {
                    remainingSeconds.toFloat() / totalSeconds.toFloat()
                } else 0f
            },
            modifier = Modifier.size(ProgressIndicatorSize),
            color = when (sessionType) {
                SessionType.WORK -> MaterialTheme.colorScheme.primary
                SessionType.SHORT_BREAK -> MaterialTheme.colorScheme.secondary
                SessionType.LONG_BREAK -> MaterialTheme.colorScheme.tertiary
            },
            strokeWidth = ProgressIndicatorStrokeWidth,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )

        Text(
            text = formatTime(remainingSeconds),
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = TimerTextSize,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun CompletedSessionsText(
    sessionType: SessionType,
    completedWorkSessions: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = if (sessionType == SessionType.WORK) {
            pluralStringResource(
                Res.plurals.sessions_count_format,
                completedWorkSessions,
                completedWorkSessions
            )
        } else "",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@Composable
private fun TimerControls(
    timerState: TimerState,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(PaddingLarge),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(
            onClick = onResetClick,
            modifier = Modifier.size(ButtonSizeMedium)
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = stringResource(Res.string.reset),
                modifier = Modifier.size(IconSizeMedium)
            )
        }

        LargeFloatingActionButton(
            onClick = {
                if (timerState == TimerState.RUNNING) {
                    onPauseClick()
                } else {
                    onStartClick()
                }
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                imageVector = if (timerState == TimerState.RUNNING) {
                    Icons.Default.Pause
                } else {
                    Icons.Default.PlayArrow
                },
                contentDescription = if (timerState == TimerState.RUNNING) {
                    stringResource(Res.string.pause)
                } else {
                    stringResource(Res.string.start)
                },
                modifier = Modifier.size(IconSizeLarge)
            )
        }
    }
}

private fun formatTime(seconds: Int): String {
    val h = seconds / SECONDS_IN_HOUR
    val m = (seconds % SECONDS_IN_HOUR) / SECONDS_IN_MINUTE
    val s = seconds % SECONDS_IN_MINUTE

    val mStr = m.toString().padStart(2, '0')
    val sStr = s.toString().padStart(2, '0')

    return if (h > 0) {
        val hStr = h.toString().padStart(2, '0')
        "$hStr:$mStr:$sStr"
    } else {
        "$mStr:$sStr"
    }
}

private const val SECONDS_IN_HOUR = 3600
private const val SECONDS_IN_MINUTE = 60

@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    MyPomodoroTheme {
        Surface {
            TimerScreen(
                uiState = TimerUiState()
            ) { }
        }
    }
}
