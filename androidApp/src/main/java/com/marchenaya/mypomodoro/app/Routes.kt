package com.marchenaya.mypomodoro.app

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Routes : NavKey {
    @Serializable
    data object Timer : Routes

    @Serializable
    data object Settings : Routes
}