package com.powerly.splash

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.powerly.lib.AppRoutes
import com.powerly.splash.update.UpdateAppDialog
import com.powerly.ui.theme.LocalMainActivity

fun NavGraphBuilder.splashDestinations(
    navController: NavHostController
) {
    composable<AppRoutes.Splash> {
        val activity = LocalMainActivity.current
        SplashScreen(
            openWelcomeScreen = {
                navController.navigate(AppRoutes.User.Welcome) {
                    popUpTo(AppRoutes.Splash) {
                        inclusive = true
                    }
                }
            },
            openHomeScreen = {
                navController.navigate(AppRoutes.Navigation) {
                    popUpTo(AppRoutes.Splash) {
                        inclusive = true
                    }
                }
            },
            openAppUpdate = { navController.navigate(AppRoutes.Splash.UpdateApp) },
            onClose = { activity?.finish() }
        )
    }

    dialog<AppRoutes.Splash.UpdateApp> {
        UpdateAppDialog()
    }
}