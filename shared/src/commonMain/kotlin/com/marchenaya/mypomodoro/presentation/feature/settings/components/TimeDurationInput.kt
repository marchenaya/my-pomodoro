package com.marchenaya.mypomodoro.presentation.feature.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.marchenaya.mypomodoro.presentation.designsystem.MyPomodoroTheme
import com.marchenaya.mypomodoro.presentation.designsystem.PaddingSmall
import mypomodoro.shared.generated.resources.Res
import mypomodoro.shared.generated.resources.hours
import mypomodoro.shared.generated.resources.minutes
import mypomodoro.shared.generated.resources.seconds
import org.jetbrains.compose.resources.stringResource

@Composable
fun TimeDurationInput(
    durationSeconds: Int,
    onDurationChange: (Int) -> Unit
) {
    var hours by remember(durationSeconds) { mutableStateOf((durationSeconds / SECONDS_IN_HOUR).toString()) }
    var minutes by remember(durationSeconds) { mutableStateOf(((durationSeconds % SECONDS_IN_HOUR) / SECONDS_IN_MINUTE).toString()) }
    var seconds by remember(durationSeconds) { mutableStateOf((durationSeconds % SECONDS_IN_MINUTE).toString()) }

    fun updateDuration(hStr: String, mStr: String, sStr: String) {
        val h = hStr.toIntOrNull() ?: 0
        val m = mStr.toIntOrNull() ?: 0
        val s = sStr.toIntOrNull() ?: 0
        onDurationChange(h * SECONDS_IN_HOUR + m * SECONDS_IN_MINUTE + s)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PaddingSmall)
    ) {
        TimeUnitField(
            value = hours,
            onValueChange = {
                if (it.length <= MAX_LENGTH && it.all { char -> char.isDigit() }) {
                    hours = it
                    updateDuration(it, minutes, seconds)
                }
            },
            label = stringResource(Res.string.hours),
            modifier = Modifier.weight(1f)
        )
        TimeUnitField(
            value = minutes,
            onValueChange = {
                if (it.length <= MAX_LENGTH && it.all { char -> char.isDigit() }) {
                    minutes = it
                    updateDuration(hours, it, seconds)
                }
            },
            label = stringResource(Res.string.minutes),
            modifier = Modifier.weight(1f)
        )
        TimeUnitField(
            value = seconds,
            onValueChange = {
                if (it.length <= MAX_LENGTH && it.all { char -> char.isDigit() }) {
                    seconds = it
                    updateDuration(hours, minutes, it)
                }
            },
            label = stringResource(Res.string.seconds),
            modifier = Modifier.weight(1f)
        )
    }
}

private const val SECONDS_IN_HOUR = 3600
private const val SECONDS_IN_MINUTE = 60
private const val MAX_LENGTH = 2

@Preview
@Composable
private fun TimeDurationInputPreview() {
    MyPomodoroTheme {
        TimeDurationInput(
            durationSeconds = 120,
            onDurationChange = {}
        )
    }
}