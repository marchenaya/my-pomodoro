package com.marchenaya.mypomodoro.data.mapper

import com.marchenaya.mypomodoro.data.model.SessionTypeSerializable
import com.marchenaya.mypomodoro.domain.model.SessionType

fun SessionType.toSessionTypeSerializable(): SessionTypeSerializable {
    return when (this) {
        SessionType.WORK -> SessionTypeSerializable.WORK
        SessionType.SHORT_BREAK -> SessionTypeSerializable.SHORT_BREAK
        SessionType.LONG_BREAK -> SessionTypeSerializable.LONG_BREAK
    }
}

fun SessionTypeSerializable.toSessionType(): SessionType {
    return when (this) {
        SessionTypeSerializable.WORK -> SessionType.WORK
        SessionTypeSerializable.SHORT_BREAK -> SessionType.SHORT_BREAK
        SessionTypeSerializable.LONG_BREAK -> SessionType.LONG_BREAK
    }
}