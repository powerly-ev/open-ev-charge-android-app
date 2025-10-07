package com.powerly.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import com.powerly.account.invite.InviteScreen
import com.powerly.account.language.LanguagesDialog
import com.powerly.account.profile.ProfileScreen
import com.powerly.lib.AppRoutes

fun NavGraphBuilder.accountDestinations(
    navController: NavHostController
) {
    navigation<AppRoutes.Account>(startDestination = AppRoutes.Account.Profile) {
        composable<AppRoutes.Account.Profile> {
            ProfileScreen(
                navigateToWelcomeScreen = {
                    navController.navigate(AppRoutes.User.Welcome)
                },
                onClose = {
                    navController.popBackStack()
                }
            )
        }

        composable<AppRoutes.Account.Invite> {
            InviteScreen(
                onBack = { navController.popBackStack() }
            )
        }

        dialog<AppRoutes.Account.Language> {
            LanguagesDialog(
                onDismiss = { navController.popBackStack() }
            )
        }
    }
}
