package com.powerly.splash

import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.powerly.lib.AppRoutes
import com.powerly.splash.update.UpdateAppDialog
import com.powerly.ui.extensions.finish

fun NavGraphBuilder.splashDestinations(
    navController: NavHostController,
    onRefreshUser: () -> Unit
) {
    composable<AppRoutes.Splash> {
        val context = LocalContext.current
        SplashScreen(
            openWelcomeScreen = {
                navController.navigate(AppRoutes.User.Welcome) {
                    popUpTo(AppRoutes.Splash) {
                        inclusive = true
                    }
                }
            },
            openHomeScreen = { extras ->
                onRefreshUser()
                navController.navigate(AppRoutes.Navigation) {
                    popUpTo(AppRoutes.Splash) {
                        inclusive = true
                    }
                }
            },
            openAppUpdate = { navController.navigate(AppRoutes.Splash.UpdateApp) },
            onClose = { context.finish() }
        )
    }

    dialog<AppRoutes.Splash.UpdateApp> {
        UpdateAppDialog()
    }
}