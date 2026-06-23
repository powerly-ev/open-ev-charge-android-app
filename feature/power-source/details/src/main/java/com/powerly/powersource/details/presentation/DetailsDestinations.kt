package com.powerly.powersource.details.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.powerly.navigation.AppRoutes
import com.powerly.powersource.charging.presentation.chargingDestinations
import com.powerly.powersource.details.presentation.boarding.OnBoardingDialog
import com.powerly.powersource.details.presentation.media.MediaScreen
import com.powerly.powersource.details.presentation.reviews.ReviewScreen
import com.powerly.powersource.details.presentation.source.PowerSourceScreen
import com.powerly.powersource.details.presentation.source.SourceEvents
import com.powerly.ui.HomeUiState
import com.powerly.ui.navigation.sharedGraphViewModel

fun NavGraphBuilder.powerSourceDestinations(
    navController: NavHostController,
    uiState: HomeUiState,
    openActiveOrders: () -> Unit,
    openCompletedOrders: () -> Unit
) {
    navigation<AppRoutes.PowerSource>(startDestination = AppRoutes.PowerSource.Details("")) {
        composable<AppRoutes.PowerSource.Details> { entry ->
            val viewModel = entry.sharedGraphViewModel<DetailsViewModel>(
                navController,
                parentRoute = AppRoutes.PowerSource
            )
            val ps = entry.toRoute<AppRoutes.PowerSource.Details>()
            PowerSourceScreen(
                powerSourceId = ps.id,
                viewModel = viewModel,
                uiState = uiState,
                onNavigate = {
                    when (it) {
                        is SourceEvents.Charge ->
                            navController.navigate(AppRoutes.PowerSource.ChargingDialog)

                        is SourceEvents.HowToCharge ->
                            navController.navigate(AppRoutes.PowerSource.OnBoarding)

                        is SourceEvents.Balance ->
                            navController.navigate(AppRoutes.Payment.Balance.Show)

                        is SourceEvents.Close -> navController.popBackStack()

                        is SourceEvents.Reviews ->
                            navController.navigate(AppRoutes.PowerSource.Reviews(ps.id))

                        is SourceEvents.Media ->
                            navController.navigate(AppRoutes.PowerSource.Media(ps.id))

                        else -> {}
                    }
                }
            )
        }

        dialog<AppRoutes.PowerSource.Reviews> {
            ReviewScreen(onBack = { navController.popBackStack() })
        }

        dialog<AppRoutes.PowerSource.Media> {
            MediaScreen(onBack = { navController.popBackStack() })
        }

        dialog<AppRoutes.PowerSource.OnBoarding> {
            OnBoardingDialog(onDismiss = { navController.popBackStack() })
        }

        chargingDestinations(
            navController = navController,
            openActiveOrders = openActiveOrders,
            openCompletedOrders = openCompletedOrders,
            activePowerSource = { entry ->
                entry.sharedGraphViewModel<DetailsViewModel>(
                    navController,
                    parentRoute = AppRoutes.PowerSource
                ).powerSource
            }
        )
    }
}
