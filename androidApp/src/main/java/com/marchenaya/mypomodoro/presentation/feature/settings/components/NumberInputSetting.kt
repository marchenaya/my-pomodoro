package com.marchenaya.mypomodoro.presentation.feature.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.marchenaya.mypomodoro.presentation.designsystem.MyPomodoroTheme
import com.marchenaya.mypomodoro.presentation.designsystem.PaddingMedium
import com.marchenaya.mypomodoro.presentation.designsystem.PaddingSmall

@Composable
fun NumberInputSetting(
    label: String,
    icon: ImageVector,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    var textValue by remember(value) {
        mutableStateOf(TextFieldValue(text = value.toString(), selection = TextRange(value.toString().length)))
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = PaddingSmall)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(PaddingSmall))
        OutlinedTextField(
            value = textValue,
            onValueChange = { newValue ->
                val filteredText = newValue.text.filter { it.isDigit() }
                textValue = newValue.copy(text = filteredText)
                val intValue = filteredText.toIntOrNull()
                if (intValue != null) {
                    onValueChange(intValue)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        textValue = textValue.copy(selection = TextRange(0, textValue.text.length))
                    } else if (textValue.text.isEmpty()) {
                        textValue =
                            TextFieldValue(text = DEFAULT_VALUE_STR, selection = TextRange(1))
                        onValueChange(DEFAULT_VALUE_INT)
                    }
                }
        )
    }
}

private const val DEFAULT_VALUE_STR = "1"
private const val DEFAULT_VALUE_INT = 1

@Preview
@Composable
fun NumberInputSettingPreview() {
    MyPomodoroTheme {
        Surface(modifier = Modifier.padding(PaddingMedium)) {
            NumberInputSetting(
                label = "Sessions before long break",
                icon = Icons.Default.Repeat,
                value = 4,
                onValueChange = {}
            )
        }
    }
}
