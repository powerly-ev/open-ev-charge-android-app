package com.powerly

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.powerly.account.accountDestinations
import com.powerly.core.model.powerly.OrderTab
import com.powerly.core.model.util.Message
import com.powerly.main.NavigationScreen
import com.powerly.lib.AppRoutes
import com.powerly.lib.Route
import com.powerly.orders.SessionViewModel
import com.powerly.payment.balance.BalanceViewModel
import com.powerly.payment.methods.PaymentViewModel
import com.powerly.payment.paymentDestinations
import com.powerly.powerSource.PsViewModel
import com.powerly.powerSource.powersourceDestinations
import com.powerly.splash.splashDestinations
import com.powerly.ui.dialogs.message.MessageDialog
import com.powerly.user.UserViewModel
import com.powerly.user.email.EmailLoginViewModel
import com.powerly.user.userDestinations
import com.powerly.vehicles.VehiclesViewModel
import com.powerly.vehicles.vehiclesDestinations

private const val TAG = "RootGraph"

@Composable
fun RootGraph(
    modifier: Modifier = Modifier,
    startDestination: Route = AppRoutes.Splash,
    mainViewModel: MainViewModel = koinViewModel(),
    sessionsViewModel: SessionViewModel = koinViewModel(),
    paymentViewModel: PaymentViewModel = koinViewModel(),
    balanceViewModel: BalanceViewModel = koinViewModel(),
    vehiclesViewModel: VehiclesViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel(),
    emailViewModel: EmailLoginViewModel = koinViewModel(),
    psViewModel: PsViewModel = koinViewModel(),
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
            MessageDialog(
                message = message,
                onDismiss = { navController.popBackStack() }
            )
        }

        splashDestinations(
            navController = navController,
            onRefreshUser = { mainViewModel.refreshUser() }
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
            navController = navController,
            userViewModel,
            emailViewModel,
            onRefreshUser = { mainViewModel.refreshUser() }
        )

        powersourceDestinations(
            navController = navController,
            viewModel = psViewModel,
            uiState = mainViewModel.uiState,
            onRefreshUser = { mainViewModel.getUserDetails() },
            openActiveOrders = {
                // navigate to tab orders/active section
                homeTab.value = AppRoutes.Navigation.Orders(OrderTab.ACTIVE)
            },
            openCompletedOrders = {
                sessionsViewModel.refreshCompletedOrders()
                // navigate to tab orders/complete section
                homeTab.value = AppRoutes.Navigation.Orders(OrderTab.HISTORY)
            }
        )

        paymentDestinations(
            navController = navController,
            paymentViewModel,
            balanceViewModel,
            onRefreshUser = { mainViewModel.refreshUser() }
        )

        vehiclesDestinations(
            navController = navController,
            viewModel = vehiclesViewModel
        )

        accountDestinations(
            navController = navController,
            onRefreshUser = { mainViewModel.refreshUser() }
        )
    }
}




