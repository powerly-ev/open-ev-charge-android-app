package com.powerly.user

import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.powerly.lib.AppRoutes
import com.powerly.ui.dialogs.countries.CountriesDialog
import com.powerly.ui.dialogs.signIn.SignInOptions
import com.powerly.ui.extensions.openCall
import com.powerly.user.email.EmailLoginViewModel
import com.powerly.user.email.login.EmailLoginScreen
import com.powerly.user.email.password.create.EmailPasswordCreateScreen
import com.powerly.user.email.password.enter.EmailPasswordEnterScreen
import com.powerly.user.email.password.reset.EmailPasswordResetScreen
import com.powerly.user.email.verify.RegisterVerificationScreen
import com.powerly.user.welcome.WelcomeScreen
import com.powerly.user.welcome.agreement.UserAgreementDialog
import com.powerly.user.welcome.language.LanguagesDialog

fun NavGraphBuilder.userDestinations(
    navController: NavHostController,
    userViewModel: UserViewModel,
    emailViewModel: EmailLoginViewModel,
    onRefreshUser: () -> Unit
) {

    fun navigateToHome(
        loggedIn: Boolean = false,
        openProfile: Boolean = false
    ) {
        val isLoggedIn = userViewModel.isLoggedIn
        onRefreshUser()
        if (loggedIn) {
            userViewModel.cancelRegistrationReminders()
            userViewModel.updateDevice()
        }
        navController.navigate(AppRoutes.Navigation) {
            if (isLoggedIn) popUpTo(AppRoutes.User) { inclusive = true }
        }
        if (openProfile) navController.navigate(AppRoutes.Account.Profile)
    }

    navigation<AppRoutes.User>(startDestination = AppRoutes.User.Welcome) {
        composable<AppRoutes.User.Welcome> {
            WelcomeScreen(
                viewModel = userViewModel,
                navigateToHome = ::navigateToHome,
                openUserAgreement = {
                    navController.navigate(AppRoutes.User.Agreement(it))
                },
                changeAppLanguage = {
                    navController.navigate(AppRoutes.User.Welcome.Language)
                },
                navigateToLogin = {
                    when (it) {
                        SignInOptions.Email -> {
                            emailViewModel.resetInputs()
                            navController.navigate(AppRoutes.User.Email.Login)
                        }

                        else -> {}
                    }
                }
            )
        }

        composable<AppRoutes.User.Agreement> {
            val userAgreement = it.toRoute<AppRoutes.User.Agreement>()
            UserAgreementDialog(
                type = userAgreement.type,
                onDismiss = { navController.popBackStack() }
            )
        }


        dialog<AppRoutes.User.Welcome.Language> {
            LanguagesDialog(
                onDismiss = { navController.popBackStack() }
            )
        }


        /**
         * Email Login
         */

        composable<AppRoutes.User.Email.Login> {
            EmailLoginScreen(
                viewModel = emailViewModel,
                navigateToPassword = { newUser ->
                    val route = if (newUser) AppRoutes.User.Email.Password.Create
                    else AppRoutes.User.Email.Password.Enter
                    navController.navigate(route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<AppRoutes.User.Email.Password.Create> {
            EmailPasswordCreateScreen(
                userViewModel = userViewModel,
                viewModel = emailViewModel,
                navigateToVerification = {
                    val route = AppRoutes.User.Email.Verification
                    navController.navigate(route) {
                        popUpTo(AppRoutes.User.Email.Login)
                    }
                },
                onSelectCountry = {
                    navController.navigate(AppRoutes.User.Email.Country)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<AppRoutes.User.Email.Password.Enter> {
            EmailPasswordEnterScreen(
                viewModel = emailViewModel,
                navigateToHome = {
                    navigateToHome(loggedIn = true)
                },
                navigateToPasswordReset = {
                    emailViewModel.counterTimeout.intValue = it
                    navController.navigate(AppRoutes.User.Email.Password.Reset)
                },
                navigateToVerification = {
                    navController.navigate(AppRoutes.User.Email.Verification)
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable<AppRoutes.User.Email.Password.Reset> {
            EmailPasswordResetScreen(
                viewModel = emailViewModel,
                onBack = { navController.popBackStack() },
            )
        }

        composable<AppRoutes.User.Email.Verification> {
            val context = LocalContext.current
            RegisterVerificationScreen(
                viewModel = emailViewModel,
                callSupport = { context.openCall(userViewModel.supportNumber) },
                navigateToHome = {
                    navigateToHome(loggedIn = true)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<AppRoutes.User.Email.Country> {
            CountriesDialog(
                onDismiss = { navController.popBackStack() },
                selectedCountry = { userViewModel.country.value },
                onSelectCountry = userViewModel::setCountry
            )
        }
    }
}