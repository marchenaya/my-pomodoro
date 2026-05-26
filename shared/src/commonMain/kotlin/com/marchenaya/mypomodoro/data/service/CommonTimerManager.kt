package com.marchenaya.mypomodoro.data.service

import com.marchenaya.mypomodoro.data.platform.currentTimeMillis
import com.marchenaya.mypomodoro.domain.model.TimerState
import com.marchenaya.mypomodoro.domain.usecase.GetTimerStateUseCase
import com.marchenaya.mypomodoro.domain.usecase.SaveTimerStateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CommonTimerManager(
    private val getTimerStateUseCase: GetTimerStateUseCase,
    private val saveTimerStateUseCase: SaveTimerStateUseCase,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private var timerJob: Job? = null

    fun start(
        initialRemainingSeconds: Int,
        endTime: Long,
        onTick: suspend (Int) -> Unit = {},
        onFinished: suspend () -> Unit = {}
    ) {
        timerJob?.cancel()
        timerJob = scope.launch {
            var state = getTimerStateUseCase().first()

            // Update state to RUNNING if needed
            if (state.timerState != TimerState.RUNNING || state.endTime != endTime) {
                state = state.copy(
                    timerState = TimerState.RUNNING,
                    remainingSeconds = initialRemainingSeconds,
                    endTime = endTime
                )
                saveTimerStateUseCase(state)
            }

            var lastSavedRemaining = initialRemainingSeconds
            var remaining = initialRemainingSeconds

            while (remaining > 0) {
                val currentState = getTimerStateUseCase().first()
                if (currentState.timerState != TimerState.RUNNING) break

                val currentTime = getCurrentTimeMillis()
                remaining = ((endTime - currentTime) / MILLIS_IN_SECOND).toInt().coerceAtLeast(0)

                onTick(remaining)

                if (lastSavedRemaining - remaining >= SAVE_INTERVAL_SECONDS || remaining == 0) {
                    saveTimerStateUseCase(currentState.copy(remainingSeconds = remaining))
                    lastSavedRemaining = remaining
                }

                if (remaining > 0) {
                    delay(TICK_DELAY_MILLIS)
                }
            }

            if (remaining <= 0) {
                val finalState = getTimerStateUseCase().first()
                if (finalState.timerState == TimerState.RUNNING) {
                    onFinished()
                    saveTimerStateUseCase(
                        finalState.copy(
                            timerState = TimerState.IDLE,
                            remainingSeconds = 0
                        )
                    )
                }
            }
        }
    }

    fun stop() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun getCurrentTimeMillis(): Long = currentTimeMillis()

    companion object {
        private const val TICK_DELAY_MILLIS = 1000L
        private const val MILLIS_IN_SECOND = 1000L
        private const val SAVE_INTERVAL_SECONDS = 5
    }
}

// TODO: Move currentTimeMillis to a platform-specific expect/actual if System.currentTimeMillis() 
// is not available in all targets, but it should be available in commonMain for most KMP setups.
