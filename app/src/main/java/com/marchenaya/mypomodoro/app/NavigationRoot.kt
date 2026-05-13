package com.marchenaya.mypomodoro.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.marchenaya.mypomodoro.R
import com.marchenaya.mypomodoro.presentation.designsystem.ZeroDp
import com.marchenaya.mypomodoro.presentation.feature.settings.SettingsScreen
import com.marchenaya.mypomodoro.presentation.feature.timer.TimerScreenRoot

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun NavigationRoot() {
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(Route.Timer)

    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(horizontalPartitionSpacerSize = ZeroDp)
    }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)

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
                TimerScreenRoot(
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
                SettingsScreen()
            }
        }
    )
}
