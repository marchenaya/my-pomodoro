package com.marchenaya.mypomodoro.presentation.feature.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marchenaya.mypomodoro.presentation.designsystem.MyPomodoroTheme

@Composable
fun TimeDurationSetting(
    label: String,
    icon: ImageVector,
    durationSeconds: Int,
    onDurationChange: (Int) -> Unit
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
        }
        Spacer(modifier = Modifier.height(12.dp))
        TimeDurationInput(
            durationSeconds = durationSeconds,
            onDurationChange = onDurationChange
        )
    }
}

@Preview
@Composable
fun TimeDurationSettingPreview() {
    MyPomodoroTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            TimeDurationSetting(
                label = "Work Duration",
                icon = Icons.Default.Work,
                durationSeconds = 25 * 60,
                onDurationChange = {}
            )
        }
    }
}
