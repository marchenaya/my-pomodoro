package com.marchenaya.mypomodoro.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class SessionTypeSerializable {
    WORK, SHORT_BREAK, LONG_BREAK
}
