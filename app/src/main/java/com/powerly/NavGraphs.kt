package com.powerly

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.powerly.account.presentation.accountDestinations
import com.powerly.core.model.powerly.OrderTab
import com.powerly.core.model.util.Message
import com.powerly.lib.AppRoutes
import com.powerly.lib.Route
import com.powerly.main.NavigationScreen
import com.powerly.orders.presentation.SessionsViewModel
import com.powerly.payment.presentation.paymentDestinations
import com.powerly.powersource.details.presentation.powerSourceDestinations
import com.powerly.splash.presentation.splashDestinations
import com.powerly.ui.dialogs.message.ModalMessageDialog
import com.powerly.user.presentation.userDestinations
import com.powerly.vehicles.presentation.vehiclesDestinations
import org.koin.androidx.compose.koinViewModel


@Composable
fun RootGraph(
    modifier: Modifier = Modifier,
    startDestination: Route = AppRoutes.Splash,
    mainViewModel: MainViewModel = koinViewModel(),
    sessionsViewModel: SessionsViewModel = koinViewModel(),
    navController: NavHostController = rememberNavController()
) {

    val homeTab = remember { mutableStateOf<Route?>(null) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        dialog<AppRoutes.MessageDialog> {
            val message: Message = it.toRoute<AppRoutes.MessageDialog>().asMessage
            ModalMessageDialog(
                message = message,
                onDismiss = { navController.popBackStack() }
            )
        }

        splashDestinations(
            navController = navController
        )

        composable<AppRoutes.Navigation> {
            NavigationScreen(
                uiState = mainViewModel.uiState,
                rootNavController = navController,
                sessionsViewModel = sessionsViewModel,
                homeTab = homeTab
            )
        }

        userDestinations(
            navController = navController
        )

        powerSourceDestinations(
            navController = navController,
            uiState = mainViewModel.uiState,
            openActiveOrders = {
                homeTab.value = AppRoutes.Navigation.Orders(OrderTab.ACTIVE)
            },
            openCompletedOrders = {
                sessionsViewModel.refreshCompletedOrders()
                homeTab.value = AppRoutes.Navigation.Orders(OrderTab.HISTORY)
            }
        )

        paymentDestinations(
            navController = navController
        )

        vehiclesDestinations(
            navController = navController
        )

        accountDestinations(
            navController = navController
        )
    }
}
