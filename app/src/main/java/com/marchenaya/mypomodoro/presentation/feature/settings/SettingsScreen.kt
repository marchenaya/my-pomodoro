package com.marchenaya.mypomodoro.presentation.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marchenaya.mypomodoro.R
import com.marchenaya.mypomodoro.domain.repository.SettingsRepository
import com.marchenaya.mypomodoro.domain.usecase.GetSettingsUseCase
import com.marchenaya.mypomodoro.domain.usecase.SaveSettingsUseCase
import com.marchenaya.mypomodoro.presentation.theme.MyPomodoroTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    getSettingsUseCase: GetSettingsUseCase,
    saveSettingsUseCase: SaveSettingsUseCase
) {
    val workDuration by getSettingsUseCase.workDuration.collectAsState(initial = 25)
    val shortBreakDuration by getSettingsUseCase.shortBreakDuration.collectAsState(initial = 5)
    val longBreakDuration by getSettingsUseCase.longBreakDuration.collectAsState(initial = 15)
    val sessionsBeforeLongBreak by getSettingsUseCase.sessionsBeforeLongBreak.collectAsState(initial = 4)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.settings)) })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.session_durations),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DurationSetting(
                label = stringResource(R.string.work_duration),
                icon = Icons.Default.Work,
                value = workDuration,
                range = 1f..60f,
                onValueChange = { scope.launch { saveSettingsUseCase.updateWorkDuration(it.roundToInt()) } }
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)

            DurationSetting(
                label = stringResource(R.string.short_break_duration),
                icon = Icons.Default.Coffee,
                value = shortBreakDuration,
                range = 1f..20f,
                onValueChange = { scope.launch { saveSettingsUseCase.updateShortBreakDuration(it.roundToInt()) } }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)

            DurationSetting(
                label = stringResource(R.string.long_break_duration),
                icon = Icons.Default.LocalCafe,
                value = longBreakDuration,
                range = 5f..45f,
                onValueChange = { scope.launch { saveSettingsUseCase.updateLongBreakDuration(it.roundToInt()) } }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)

            DurationSetting(
                label = stringResource(R.string.sessions_before_long_break),
                icon = Icons.Default.Repeat,
                value = sessionsBeforeLongBreak,
                range = 1f..10f,
                labelValueFormat = stringResource(R.string.sessions_count_format, sessionsBeforeLongBreak),
                onValueChange = { scope.launch { saveSettingsUseCase.updateSessionsBeforeLongBreak(it.roundToInt()) } }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = stringResource(R.string.about),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = stringResource(R.string.about_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DurationSetting(
    label: String,
    icon: ImageVector,
    value: Int,
    range: ClosedFloatingPointRange<Float>,
    labelValueFormat: String = stringResource(R.string.min_format, value),
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                text = label, 
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = labelValueFormat, 
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = value.toFloat(),
            onValueChange = onValueChange,
            valueRange = range,
            steps = (range.endInclusive - range.start).toInt() - 1,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
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
    MyPomodoroTheme {
        Surface {
            SettingsScreen(
                getSettingsUseCase = GetSettingsUseCase(mockRepository),
                saveSettingsUseCase = SaveSettingsUseCase(mockRepository)
            )
        }
    }
}