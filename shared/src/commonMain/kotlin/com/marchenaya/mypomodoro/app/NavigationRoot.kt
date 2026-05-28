package com.marchenaya.mypomodoro.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.marchenaya.mypomodoro.presentation.designsystem.ZeroDp
import com.marchenaya.mypomodoro.presentation.feature.settings.SettingsScreen
import com.marchenaya.mypomodoro.presentation.feature.timer.TimerScreenRoot
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import mypomodoro.shared.generated.resources.Res
import mypomodoro.shared.generated.resources.select_settings_to_view_more
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun NavigationRoot() {
    val nav3Configuration = remember {
        SavedStateConfiguration {
            this.serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Routes.Timer::class)
                    subclass(Routes.Settings::class)
                }
            }
        }
    }
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(
        configuration = nav3Configuration,
        Routes.Timer
    )

    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
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
            entry<Routes.Timer>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(stringResource(Res.string.select_settings_to_view_more))
                        }
                    }
                )
            ) {
                TimerScreenRoot(
                    onSettingsClick = {
                        if (backStack.none { it is Routes.Settings }) {
                            backStack.add(Routes.Settings)
                        }
                    }
                )
            }
            entry<Routes.Settings>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) {
                SettingsScreen(
                    onBack = if (directive.maxHorizontalPartitions == 1) {
                        { backStack.removeLastOrNull() }
                    } else {
                        null
                    }
                )
            }
        }
    )
}
