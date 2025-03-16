package com.powerly.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import com.powerly.account.invite.InviteScreen
import com.powerly.account.language.LanguagesDialog
import com.powerly.account.profile.ProfileScreen
import com.powerly.account.profile.ProfileViewModel
import com.powerly.lib.AppRoutes
import com.powerly.ui.dialogs.countries.CountriesDialog

fun NavGraphBuilder.accountDestinations(
    navController: NavHostController,
    viewModel: ProfileViewModel,
    onRefreshUser: () -> Unit
) {
    navigation<AppRoutes.Account>(startDestination = AppRoutes.Account.Profile) {
        composable<AppRoutes.Account.Profile> {
            ProfileScreen(
                viewModel = viewModel,
                navigateToWelcomeScreen = {
                    onRefreshUser()
                    navController.navigate(AppRoutes.User.Welcome)
                },
                selectedCountry = {
                    navController.navigate(AppRoutes.Account.Profile.Country)
                },
                onClose = { profileUpdated ->
                    if (profileUpdated) onRefreshUser()
                    navController.popBackStack()
                }
            )
        }

        dialog<AppRoutes.Account.Profile.Country> {
            val viewModel = viewModel
            CountriesDialog(
                onDismiss = { navController.popBackStack() },
                selectedCountry = viewModel::getUserCountry,
                onSelectCountry = viewModel::updateUserCountry
            )
        }

        composable<AppRoutes.Account.Invite> {
            InviteScreen(
                viewModel = viewModel,
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
