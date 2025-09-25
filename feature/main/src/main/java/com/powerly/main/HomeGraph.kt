package com.powerly.main

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.powerly.account.AccountEvents
import com.powerly.account.AccountScreen
import com.powerly.core.model.powerly.OrderTab
import com.powerly.core.model.powerly.Session
import com.powerly.home.MyMapViewModel
import com.powerly.home.home.HomeScreen
import com.powerly.home.home.HomeViewModel
import com.powerly.home.home.NavigationEvents
import com.powerly.home.map.MapScreen
import com.powerly.lib.AppRoutes
import com.powerly.lib.Route
import com.powerly.orders.OrdersScreen
import com.powerly.orders.SessionViewModel
import com.powerly.orders.history.details.DeliveredOrderScreen
import com.powerly.resources.R
import com.powerly.scan.ScannerScreen
import com.powerly.ui.HomeUiState
import com.powerly.ui.extensions.intent
import com.powerly.ui.util.rememberActivityResultState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.compose.viewmodel.koinViewModel

private const val TAG = "HomeGraph"

@Composable
fun HomeGraph(
    tab: Route,
    uiState: HomeUiState,
    sessionsViewModel: SessionViewModel,
    rootNavController: NavHostController,
    innerNavController: NavHostController,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current as ComponentActivity
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()
    val mapViewModel: MyMapViewModel = koinViewModel(viewModelStoreOwner = activity)
    val homeViewModel: HomeViewModel = koinViewModel(viewModelStoreOwner = activity)
    var doOnce by rememberSaveable { mutableStateOf(true) }
    var sessionToReview by rememberSaveable { mutableStateOf("") }
    val activityResult = rememberActivityResultState()

    fun sessionFeedBack(
        session: Session,
        openHistory: Boolean = true
    ) {
        // to avoid multiple feedback for the same session
        if (session.id == sessionToReview) return
        sessionToReview = session.id
        Log.i(TAG, "sessionFeedBack ${session.id} - openHistory = $openHistory")

        // navigate to completed sessions
        if (openHistory) {
            sessionsViewModel.refreshCompletedOrders()
            val route = AppRoutes.Navigation.Orders(OrderTab.HISTORY)
            innerNavController.navigate(route)
        }
        coroutineScope.launch {
            delay(2000)
            // show feedback dialog
            val route = AppRoutes.PowerSource.Feedback(
                sessionId = session.id,
                title = session.chargePointName
            )
            rootNavController.navigate(route)
        }
    }

    fun openSupport() {
        uriHandler.openUri(context.getString(R.string.community_link))
    }

    // open a power source passed in a deep link
    LaunchedEffect(Unit) {
        if (homeViewModel.isLoggedIn.not()) return@LaunchedEffect
        val powerSource = homeViewModel.getPowerSourceFromDeepLink(context.intent)
        if (powerSource != null) {
            rootNavController.navigate(NavigationEvents.SourceDetails(powerSource))
        }

        if (doOnce) {
            doOnce = false
            launch {
                delay(2000)
                val order = sessionsViewModel.getPendingOrderToReview()
                if (order != null) sessionFeedBack(order, openHistory = false)
            }
        }
    }

    NavHost(
        innerNavController,
        startDestination = tab,
        modifier = Modifier.fillMaxSize(),
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        composable<AppRoutes.Navigation.Home> {
            HomeScreen(
                uiState = uiState,
                activityResult = activityResult,
                mapViewModel = mapViewModel,
                navigate = {
                    when (it) {
                        is NavigationEvents.Balance -> {
                            rootNavController.navigate(AppRoutes.Payment.Balance.Show)
                        }

                        is NavigationEvents.Login -> {
                            rootNavController.navigate(AppRoutes.User.Email.Login)
                        }

                        is NavigationEvents.Map -> {
                            mapViewModel.setSelectedPowerSource(it.source)
                            val route = AppRoutes.Navigation.Home.Map
                            innerNavController.navigate(route)
                        }

                        is NavigationEvents.SourceDetails -> {
                            val route = AppRoutes.PowerSource.Details(it.source.id)
                            rootNavController.navigate(route)
                        }

                        is NavigationEvents.Support -> openSupport()
                    }
                }
            )
        }
        composable<AppRoutes.Navigation.Orders> { backStackEntry ->
            val order: AppRoutes.Navigation.Orders = backStackEntry.toRoute()
            OrdersScreen(
                orderType = order.type,
                sessionsViewModel = sessionsViewModel,
                openHistoryScreen = { sessionFeedBack(it) },
                openChargingScreen = {
                    val route = AppRoutes.PowerSource.Charging(
                        chargePointId = it.chargePointId,
                        connector = it.connectorNumber,
                        orderId = it.id,
                        quantity = ""
                    )
                    rootNavController.navigate(route)
                },
                openPowerSource = { chargePointId ->
                    val route = AppRoutes.PowerSource.Details(id = chargePointId)
                    rootNavController.navigate(route)
                },
                openOrderDetails = {
                    sessionsViewModel.setSession(it)
                    val route = AppRoutes.Navigation.Orders.Details
                    innerNavController.navigate(route)
                }
            )
        }

        dialog<AppRoutes.Navigation.Orders.Details> {
            DeliveredOrderScreen(
                viewModel = sessionsViewModel,
                onBack = { innerNavController.popBackStack() }
            )
        }

        composable<AppRoutes.Navigation.Scan> {
            ScannerScreen(
                onBack = { innerNavController.popBackStack() },
                openPowerSource = {
                    val route = AppRoutes.PowerSource.Details(it.id)
                    rootNavController.navigate(route)
                }
            )
        }

        composable<AppRoutes.Navigation.Home.Map>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = LinearEasing
                    )
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = LinearEasing
                    )
                )
            }
        ) {
            MapScreen(
                uiState = uiState,
                viewModel = mapViewModel,
                showBalance = {
                    rootNavController.navigate(AppRoutes.Payment.Balance.Show)
                },
                openPowerSource = {
                    val route = AppRoutes.PowerSource.Details(it.id)
                    rootNavController.navigate(route)
                },
                openSupport = ::openSupport,
                onBack = { innerNavController.popBackStack() }
            )
        }

        composable<AppRoutes.Navigation.Account> {
            val isUser = remember { uiState.isLoggedIn.value }
            AccountScreen(
                uiState = uiState,
                uiEvents = {
                    when (it) {
                        AccountEvents.OpenProfile -> {
                            rootNavController.navigate(AppRoutes.Account.Profile)
                        }

                        AccountEvents.LanguageDialog -> {
                            rootNavController.navigate(AppRoutes.Account.Language)
                        }

                        AccountEvents.OpenBalance -> {
                            rootNavController.navigate(AppRoutes.Payment.Balance.Show)
                        }

                        AccountEvents.OpenSupport -> openSupport()

                        AccountEvents.OpenInvite -> {
                            rootNavController.navigate(AppRoutes.Account.Invite)
                        }

                        AccountEvents.OpenVehicles -> {
                            rootNavController.navigate(AppRoutes.Vehicles.List)
                        }

                        AccountEvents.OpenWallet -> {
                            if (isUser) {
                                rootNavController.navigate(AppRoutes.Payment.Wallet)
                            }
                        }
                    }
                }
            )
        }
    }
}

