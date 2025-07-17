package com.powerly.main

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.powerly.lib.AppRoutes
import com.powerly.lib.Route
import com.powerly.resources.R

internal sealed class NavScreens(
    val route: Route,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
) {
    data object Home : NavScreens(
        AppRoutes.Navigation.Home,
        R.string.home,
        R.drawable.home
    )

    data object Scan : NavScreens(
        AppRoutes.Navigation.Scan,
        R.string.scan,
        R.drawable.scan
    )

    data object Orders : NavScreens(
        AppRoutes.Navigation.Orders(),
        R.string.orders,
        R.drawable.cart
    )

    data object Account : NavScreens(
        AppRoutes.Navigation.Account,
        R.string.account,
        R.drawable.account
    )
}

internal fun Route.isSelected(screen: NavScreens): Boolean {
    return this == screen.route || (screen.route == AppRoutes.Navigation.Home
            && this == AppRoutes.Navigation.Home.Map)
}

internal fun NavBackStackEntry?.asMyRoute(): Route? {
    val route = this?.destination?.route.orEmpty()
    return when {
        route.contains("Home.Map") -> return AppRoutes.Navigation.Home.Map
        route.contains("Home") -> return AppRoutes.Navigation.Home
        route.contains("Scan") -> return AppRoutes.Navigation.Scan
        route.contains("Orders") -> return this?.toRoute<AppRoutes.Navigation.Orders>()
        route.contains("Account") -> return AppRoutes.Navigation.Account
        else -> null
    }
}

internal fun NavHostController.navigateToTab(
    destination: Route,
    current: Route? = null
) {
    Log.i("NavigationScreen", "navigateToTab: $destination")
    // don't do anything if the destination is the same
    if (destination == current) return
    // if user at Map screen and tabbed on Home icon, just pop back stack
    else if (destination == AppRoutes.Navigation.Home &&
        current == AppRoutes.Navigation.Home.Map
    ) {
        this.popBackStack()
    } else this.navigate(destination) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // re-selecting the same item
        launchSingleTop = true
        // Restore state when re-selecting a previously selected item
        //restoreState = true
    }
}
