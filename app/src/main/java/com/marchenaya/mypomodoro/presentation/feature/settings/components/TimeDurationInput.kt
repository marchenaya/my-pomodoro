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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marchenaya.mypomodoro.R
import com.marchenaya.mypomodoro.presentation.theme.MyPomodoroTheme

@Composable
fun TimeDurationInput(
    durationSeconds: Int,
    onDurationChange: (Int) -> Unit
) {
    var hours by remember(durationSeconds) { mutableStateOf((durationSeconds / 3600).toString()) }
    var minutes by remember(durationSeconds) { mutableStateOf(((durationSeconds % 3600) / 60).toString()) }
    var seconds by remember(durationSeconds) { mutableStateOf((durationSeconds % 60).toString()) }

    fun updateDuration(hStr: String, mStr: String, sStr: String) {
        val h = hStr.toIntOrNull() ?: 0
        val m = mStr.toIntOrNull() ?: 0
        val s = sStr.toIntOrNull() ?: 0
        onDurationChange(h * 3600 + m * 60 + s)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TimeUnitField(
            value = hours,
            onValueChange = {
                if (it.length <= 2 && it.all { char -> char.isDigit() }) {
                    hours = it
                    updateDuration(it, minutes, seconds)
                }
            },
            label = stringResource(R.string.hours),
            modifier = Modifier.weight(1f)
        )
        TimeUnitField(
            value = minutes,
            onValueChange = {
                if (it.length <= 2 && it.all { char -> char.isDigit() }) {
                    minutes = it
                    updateDuration(hours, it, seconds)
                }
            },
            label = stringResource(R.string.minutes),
            modifier = Modifier.weight(1f)
        )
        TimeUnitField(
            value = seconds,
            onValueChange = {
                if (it.length <= 2 && it.all { char -> char.isDigit() }) {
                    seconds = it
                    updateDuration(hours, minutes, it)
                }
            },
            label = stringResource(R.string.seconds),
            modifier = Modifier.weight(1f)
        )
    }
}

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