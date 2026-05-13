package com.marchenaya.mypomodoro.presentation.feature.settings.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.marchenaya.mypomodoro.presentation.designsystem.MyPomodoroTheme

@Composable
fun TimeUnitField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember(value) {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            val filteredText = newValue.text.filter { it.isDigit() }
            if (filteredText.length <= 2) {
                textFieldValue = newValue.copy(text = filteredText)
                onValueChange(filteredText)
            }
        },
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = modifier.onFocusChanged { focusState ->
            if (focusState.isFocused) {
                textFieldValue = if (textFieldValue.text == "0") {
                    TextFieldValue(text = "", selection = TextRange(0))
                } else {
                    textFieldValue.copy(selection = TextRange(0, textFieldValue.text.length))
                }
            } else {
                if (textFieldValue.text.isEmpty()) {
                    textFieldValue = TextFieldValue(text = "0", selection = TextRange(1))
                    onValueChange("0")
                }
            }
        }
    )
}

@Preview
@Composable
private fun TimeUnitFieldPreview() {
    MyPomodoroTheme {
        TimeUnitField(
            value = "12",
            onValueChange = {},
            label = "Hours"
        )
    }
}
