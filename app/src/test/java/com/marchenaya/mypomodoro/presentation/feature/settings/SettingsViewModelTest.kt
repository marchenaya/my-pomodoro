package com.marchenaya.mypomodoro.presentation.feature.settings

import com.marchenaya.mypomodoro.domain.usecase.GetSettingsUseCase
import com.marchenaya.mypomodoro.domain.usecase.SaveSettingsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val getSettingsUseCase: GetSettingsUseCase = mockk()
    private val saveSettingsUseCase: SaveSettingsUseCase = mockk()
    private lateinit var viewModel: SettingsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getSettingsUseCase.workDuration } returns flowOf(25 * 60)
        every { getSettingsUseCase.shortBreakDuration } returns flowOf(5 * 60)
        every { getSettingsUseCase.longBreakDuration } returns flowOf(15 * 60)
        every { getSettingsUseCase.sessionsBeforeLongBreak } returns flowOf(4)

        coEvery { saveSettingsUseCase.updateWorkDuration(any()) } returns Unit
        coEvery { saveSettingsUseCase.updateShortBreakDuration(any()) } returns Unit
        coEvery { saveSettingsUseCase.updateLongBreakDuration(any()) } returns Unit
        coEvery { saveSettingsUseCase.updateSessionsBeforeLongBreak(any()) } returns Unit

        viewModel = SettingsViewModel(getSettingsUseCase, saveSettingsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should collect settings and update uiState`() = runTest {
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(25 * 60, state.workDuration)
        assertEquals(5 * 60, state.shortBreakDuration)
        assertEquals(15 * 60, state.longBreakDuration)
        assertEquals(4, state.sessionsBeforeLongBreak)
    }

    @Test
    fun `onAction UpdateWorkDuration should call saveSettingsUseCase`() = runTest {
        coVerify(exactly = 0) { saveSettingsUseCase.updateWorkDuration(any()) }
        viewModel.onAction(SettingsAction.UpdateWorkDuration(30 * 60))
        advanceUntilIdle()
        coVerify { saveSettingsUseCase.updateWorkDuration(30 * 60) }
    }

    @Test
    fun `onAction UpdateShortBreakDuration should call saveSettingsUseCase`() = runTest {
        viewModel.onAction(SettingsAction.UpdateShortBreakDuration(10 * 60))
        advanceUntilIdle()
        coVerify { saveSettingsUseCase.updateShortBreakDuration(10 * 60) }
    }

    @Test
    fun `onAction UpdateLongBreakDuration should call saveSettingsUseCase`() = runTest {
        viewModel.onAction(SettingsAction.UpdateLongBreakDuration(20 * 60))
        advanceUntilIdle()
        coVerify { saveSettingsUseCase.updateLongBreakDuration(20 * 60) }
    }

    @Test
    fun `onAction UpdateSessionsBeforeLongBreak should call saveSettingsUseCase`() = runTest {
        viewModel.onAction(SettingsAction.UpdateSessionsBeforeLongBreak(5))
        advanceUntilIdle()
        coVerify { saveSettingsUseCase.updateSessionsBeforeLongBreak(5) }
    }
}
