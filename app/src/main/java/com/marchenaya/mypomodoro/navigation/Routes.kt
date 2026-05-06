package com.marchenaya.mypomodoro.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Route : NavKey {
    @Serializable
    data object Timer : Route

    @Serializable
    data object Settings : Route
}