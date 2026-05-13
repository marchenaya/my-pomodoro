package com.marchenaya.mypomodoro.presentation.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = BODY_LARGE_FONT_SIZE.sp,
        lineHeight = BODY_LARGE_LINE_HEIGHT.sp,
        letterSpacing = BODY_LARGE_LETTER_SPACING.sp
    )
)

private const val BODY_LARGE_FONT_SIZE = 16
private const val BODY_LARGE_LINE_HEIGHT = 24
private const val BODY_LARGE_LETTER_SPACING = 0.5
