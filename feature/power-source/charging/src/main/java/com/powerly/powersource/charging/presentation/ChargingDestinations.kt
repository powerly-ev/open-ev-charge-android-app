package com.powerly.powersource.charging.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.powerly.core.model.powerly.PowerSource
import com.powerly.navigation.AppRoutes
import com.powerly.powersource.charging.presentation.activate.ActivateChargerDialog
import com.powerly.powersource.charging.presentation.feedback.FeedbackDialog
import com.powerly.powersource.charging.presentation.session.ChargingScreen

fun NavGraphBuilder.chargingDestinations(
    navController: NavHostController,
    openActiveOrders: () -> Unit,
    openCompletedOrders: () -> Unit,
    activePowerSource: @Composable (NavBackStackEntry) -> PowerSource?,
) {
    dialog<AppRoutes.PowerSource.ChargingDialog> { entry ->
        val powerSource = activePowerSource(entry)
        ActivateChargerDialog(
            powerSource = { powerSource },
            onDismiss = { navController.popBackStack() },
            onOpenChargingScreen = { orderId ->
                navController.navigate(AppRoutes.PowerSource.Charging(orderId))
            }
        )
    }

    composable<AppRoutes.PowerSource.Charging> {
        ChargingScreen(
            openSessionHistory = { session ->
                navController.popBackStack(
                    AppRoutes.Navigation,
                    inclusive = false
                )
                openCompletedOrders()
                navController.navigate(
                    AppRoutes.PowerSource.Feedback(session.id, session.chargePointName)
                )
            },
            openActiveSessions = {
                navController.popBackStack(
                    AppRoutes.Navigation,
                    inclusive = false
                )
                openActiveOrders()
            },
            onBack = { navController.popBackStack() }
        )
    }

    dialog<AppRoutes.PowerSource.Feedback> {
        FeedbackDialog(
            onDismiss = { navController.popBackStack() }
        )
    }
}
