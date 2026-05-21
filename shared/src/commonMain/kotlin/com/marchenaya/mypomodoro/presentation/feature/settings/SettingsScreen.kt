package com.marchenaya.mypomodoro.presentation.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.marchenaya.mypomodoro.domain.model.Settings
import com.marchenaya.mypomodoro.presentation.designsystem.DividerThickness
import com.marchenaya.mypomodoro.presentation.designsystem.MyPomodoroTheme
import com.marchenaya.mypomodoro.presentation.designsystem.PaddingExtraLarge
import com.marchenaya.mypomodoro.presentation.designsystem.PaddingMedium
import com.marchenaya.mypomodoro.presentation.designsystem.PaddingSmall
import com.marchenaya.mypomodoro.presentation.feature.settings.components.NumberInputSetting
import com.marchenaya.mypomodoro.presentation.feature.settings.components.TimeDurationSetting
import com.marchenaya.mypomodoro.presentation.util.koinViewModel
import mypomodoro.shared.generated.resources.Res
import mypomodoro.shared.generated.resources.about
import mypomodoro.shared.generated.resources.about_description
import mypomodoro.shared.generated.resources.long_break_duration
import mypomodoro.shared.generated.resources.session_durations
import mypomodoro.shared.generated.resources.sessions_before_long_break
import mypomodoro.shared.generated.resources.settings
import mypomodoro.shared.generated.resources.short_break_duration
import mypomodoro.shared.generated.resources.work_duration
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsContent(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onAction: (SettingsAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(Res.string.settings)) })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(PaddingMedium)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(Res.string.session_durations),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = PaddingMedium)
            )

            TimeDurationSetting(
                label = stringResource(Res.string.work_duration),
                icon = Icons.Default.Work,
                durationSeconds = uiState.workDuration,
                onDurationChange = { onAction(SettingsAction.UpdateWorkDuration(it)) }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = PaddingMedium),
                thickness = DividerThickness
            )

            TimeDurationSetting(
                label = stringResource(Res.string.short_break_duration),
                icon = Icons.Default.Coffee,
                durationSeconds = uiState.shortBreakDuration,
                onDurationChange = { onAction(SettingsAction.UpdateShortBreakDuration(it)) }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = PaddingMedium),
                thickness = DividerThickness
            )

            TimeDurationSetting(
                label = stringResource(Res.string.long_break_duration),
                icon = Icons.Default.LocalCafe,
                durationSeconds = uiState.longBreakDuration,
                onDurationChange = { onAction(SettingsAction.UpdateLongBreakDuration(it)) }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = PaddingMedium),
                thickness = DividerThickness
            )

            NumberInputSetting(
                label = stringResource(Res.string.sessions_before_long_break),
                icon = Icons.Default.Repeat,
                value = uiState.sessionsBeforeLongBreak,
                onValueChange = { onAction(SettingsAction.UpdateSessionsBeforeLongBreak(it)) }
            )

            Spacer(modifier = Modifier.height(PaddingExtraLarge))

            Text(
                text = stringResource(Res.string.about),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = PaddingSmall)
            )
            Text(
                text = stringResource(Res.string.about_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    val settings = Settings()
    MyPomodoroTheme {
        Surface {
            SettingsContent(
                uiState = SettingsUiState(
                    workDuration = settings.workDurationSeconds,
                    shortBreakDuration = settings.shortBreakDurationSeconds,
                    longBreakDuration = settings.longBreakDurationSeconds,
                    sessionsBeforeLongBreak = settings.sessionsBeforeLongBreak
                ),
                onAction = {}
            )
        }
    }
}
