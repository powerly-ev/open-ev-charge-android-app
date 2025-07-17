package com.powerly.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.powerly.lib.AppRoutes
import com.powerly.lib.Route
import com.powerly.orders.SessionViewModel
import com.powerly.ui.HomeUiState
import com.powerly.ui.theme.AppTheme

@Preview
@Composable
private fun NavigationScreenPreview() {
    AppTheme {
        Footer(
            navController = rememberNavController(),
            isLoggedIn = { true }
        )
    }
}

private const val TAG = "NavigationScreen"

@Composable
fun NavigationScreen(
    uiState: HomeUiState,
    sessionsViewModel: SessionViewModel,
    homeTab: MutableState<Route?>,
    rootNavController: NavHostController,
) {
    val innerNavController = rememberNavController()

    LaunchedEffect(Unit) {
        snapshotFlow { homeTab.value }.collect { route ->
            if (route != null) {
                val currentTab = innerNavController.currentBackStackEntry?.asMyRoute()
                Log.v(TAG, "new-homeTab = $route, currentTab = $currentTab")
                innerNavController.navigateToTab(route, currentTab)
                // if this value non null, it gonna make repetitive navigation
                // for each time user navigate to home tab
                homeTab.value = null
            }
        }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Footer(
                navController = innerNavController,
                isLoggedIn = { uiState.isLoggedIn.value }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            HomeGraph(
                tab = AppRoutes.Navigation.Home,
                uiState = uiState,
                sessionsViewModel = sessionsViewModel,
                rootNavController = rootNavController,
                innerNavController = innerNavController,
            )
        }
    }
}


@Composable
private fun Footer(
    isLoggedIn: () -> Boolean,
    navController: NavHostController
) {
    val screens = mutableListOf(
        NavScreens.Home,
        NavScreens.Scan,
        NavScreens.Account
    )
    if (isLoggedIn()) screens.add(2, NavScreens.Orders)
    var currentRoute by remember { mutableStateOf(NavScreens.Home.route) }

    LaunchedEffect(Unit) {
        navController.currentBackStackEntryFlow.collect { stackEntryState ->
            stackEntryState.asMyRoute()?.let { route ->
                Log.v(TAG, "currentRoute = $route")
                currentRoute = route
            }
        }
    }

    Box(Modifier.background(color = MaterialTheme.colorScheme.background)) {
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 5.dp,
            modifier = Modifier.clip(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomEnd = 0.dp,
                    bottomStart = 0.dp
                )
            )
        ) {
            screens.forEach { screen ->
                val title = stringResource(id = screen.title)
                val image = painterResource(id = screen.icon)
                val isSelected = currentRoute.isSelected(screen)

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        navController.navigateToTab(
                            destination = screen.route,
                            current = currentRoute
                        )
                    },
                    label = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                    },
                    icon = {
                        Icon(
                            painter = image,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(top = 4.dp, end = 4.dp)
                                .size(24.dp)
                        )
                    }, colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.White,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        unselectedTextColor = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        }
    }
}


