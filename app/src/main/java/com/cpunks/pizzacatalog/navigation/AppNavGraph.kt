package com.cpunks.pizzacatalog.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.cpunks.pizzacatalog.feature.catalog.CatalogScreen
import com.cpunks.pizzacatalog.feature.catalog.CatalogViewModel
import com.cpunks.pizzacatalog.feature.splash.SplashScreen
import kotlinx.serialization.Serializable

@Serializable
data object SplashKey : NavKey
@Serializable
data object CatalogKey : NavKey

@Composable
fun AppNavGraph() {
    val backStack = rememberNavBackStack(SplashKey)

    val catalogViewModel: CatalogViewModel = hiltViewModel()
    val state by catalogViewModel.uiState.collectAsStateWithLifecycle()

    BackHandler(enabled = backStack.size == 1) {}

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<SplashKey> {
                SplashScreen(
                    ready = state.imagesReady,
                    onFinished = {
                        backStack.clear()
                        backStack.add(CatalogKey)
                    }
                )
            }
            entry<CatalogKey> {
                CatalogScreen(viewModel = catalogViewModel)
            }
        }
    )
}
