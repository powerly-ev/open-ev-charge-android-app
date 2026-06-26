package com.powerly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel

/**
 * Returns a [ViewModel] scoped to a parent navigation graph's back-stack entry,
 * so the same instance is shared across every destination inside that graph.
 *
 * Use this for "in-progress" / draft state that several sibling screens of a flow
 * must read and mutate (e.g. a multi-step wizard). Calling [koinViewModel] inside a
 * `composable { }` would otherwise scope the ViewModel to that single destination
 * and clear it as soon as the user navigates away.
 *
 * ```kotlin
 * composable<AppRoutes.Vehicles.New> { entry ->
 *     val draft = entry.sharedGraphViewModel<NewVehicleViewModel>(
 *         navController,
 *         parentRoute = AppRoutes.Vehicles
 *     )
 *     ...
 * }
 * ```
 *
 * @param VM the [ViewModel] type to resolve from Koin.
 * @param navController the controller hosting the graph.
 * @param parentRoute the route of the enclosing navigation graph whose back-stack
 * entry owns the shared [ViewModel] (e.g. a `@Serializable data object` route).
 */
@Composable
inline fun <reified VM : ViewModel> NavBackStackEntry.sharedGraphViewModel(
    navController: NavHostController,
    parentRoute: Any
): VM {
    val parentEntry = remember(this) {
        navController.getBackStackEntry(parentRoute)
    }
    return koinViewModel(viewModelStoreOwner = parentEntry)
}
