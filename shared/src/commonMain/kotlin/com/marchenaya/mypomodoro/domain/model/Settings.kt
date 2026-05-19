package com.marchenaya.mypomodoro.domain.model

data class Settings(
    val workDurationSeconds: Int = DEFAULT_WORK_DURATION,
    val shortBreakDurationSeconds: Int = DEFAULT_SHORT_BREAK_DURATION,
    val longBreakDurationSeconds: Int = DEFAULT_LONG_BREAK_DURATION,
    val sessionsBeforeLongBreak: Int = DEFAULT_SESSIONS_BEFORE_LONG_BREAK
) {
    companion object {
        const val SECONDS_IN_MINUTE = 60
        const val DEFAULT_WORK_DURATION = 25 * SECONDS_IN_MINUTE
        const val DEFAULT_SHORT_BREAK_DURATION = 5 * SECONDS_IN_MINUTE
        const val DEFAULT_LONG_BREAK_DURATION = 15 * SECONDS_IN_MINUTE
        const val DEFAULT_SESSIONS_BEFORE_LONG_BREAK = 4
    }
}
