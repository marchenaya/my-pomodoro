package com.marchenaya.mypomodoro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.marchenaya.mypomodoro.presentation.MyPomodoroApp
import com.marchenaya.mypomodoro.presentation.theme.MyPomodoroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyPomodoroTheme {
                MyPomodoroApp()
            }
        }
    }
}