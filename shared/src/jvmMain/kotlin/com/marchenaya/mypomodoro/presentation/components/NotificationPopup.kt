package com.marchenaya.mypomodoro.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.marchenaya.mypomodoro.presentation.designsystem.MyPomodoroTheme
import com.marchenaya.mypomodoro.presentation.designsystem.PaddingMedium
import com.marchenaya.mypomodoro.presentation.designsystem.PaddingSmall
import kotlinx.coroutines.delay
import mypomodoro.shared.generated.resources.Res
import mypomodoro.shared.generated.resources.ic_launcher
import mypomodoro.shared.generated.resources.next_step
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.awt.Toolkit

@Composable
fun NotificationPopup(
    message: String,
    onNextStepClick: () -> Unit,
    onClose: () -> Unit
) {
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val windowWidth = 350.dp
    val windowHeight = 150.dp

    // Position in bottom-right corner
    val state = rememberWindowState(
        position = WindowPosition(
            x = (screenSize.width - 400).dp,
            y = (screenSize.height - 200).dp
        ),
        size = DpSize(windowWidth, windowHeight)
    )

    Window(
        onCloseRequest = onClose,
        state = state,
        title = "Notification",
        undecorated = true,
        resizable = false,
        alwaysOnTop = true
    ) {
        MyPomodoroTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(PaddingMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_launcher),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.width(PaddingMedium))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(PaddingSmall),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Button(
                            onClick = {
                                onNextStepClick()
                                onClose()
                            }
                        ) {
                            Text(stringResource(Res.string.next_step))
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(10000) // Auto close after 10 seconds
        onClose()
    }
}
