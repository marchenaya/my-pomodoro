package com.marchenaya.mypomodoro.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.marchenaya.mypomodoro.R
import com.marchenaya.mypomodoro.domain.usecase.GetSettingsUseCase
import com.marchenaya.mypomodoro.domain.usecase.SaveSettingsUseCase
import com.marchenaya.mypomodoro.navigation.Route
import com.marchenaya.mypomodoro.presentation.feature.settings.SettingsScreen
import com.marchenaya.mypomodoro.presentation.feature.timer.TimerScreen
import com.marchenaya.mypomodoro.presentation.feature.timer.TimerViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MyPomodoroApp() {
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(Route.Timer)

    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(horizontalPartitionSpacerSize = 0.dp)
    }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            sceneStrategies = listOf(listDetailStrategy),
            entryProvider = entryProvider {
                entry<Route.Timer>(
                    metadata = ListDetailSceneStrategy.listPane(
                        detailPlaceholder = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(stringResource(R.string.select_settings_to_view_more))
                            }
                        }
                    )
                ) {
                    val timerViewModel: TimerViewModel = koinViewModel()
                    TimerScreen(
                        viewModel = timerViewModel,
                        onSettingsClick = {
                            if (backStack.none { it is Route.Settings }) {
                                backStack.add(Route.Settings)
                            }
                        }
                    )
                }
                entry<Route.Settings>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) {
                    val getSettingsUseCase: GetSettingsUseCase = koinInject()
                    val saveSettingsUseCase: SaveSettingsUseCase = koinInject()
                    SettingsScreen(
                        getSettingsUseCase = getSettingsUseCase,
                        saveSettingsUseCase = saveSettingsUseCase
                    )
                }
            }
        )
    }
}