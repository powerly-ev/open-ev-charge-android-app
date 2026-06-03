package com.powerly.user.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import androidx.navigation.toRoute
import androidx.compose.ui.platform.LocalUriHandler
import com.powerly.lib.AppRoutes
import com.powerly.ui.dialogs.countries.CountriesDialog
import com.powerly.ui.dialogs.signIn.SignInOptions
import com.powerly.ui.extensions.openUriSafely
import com.powerly.ui.navigation.sharedGraphViewModel
import com.powerly.user.presentation.email.EmailLoginViewModel
import com.powerly.user.presentation.email.login.EmailLoginScreen
import com.powerly.user.presentation.email.password.create.EmailPasswordCreateScreen
import com.powerly.user.presentation.email.password.enter.EmailPasswordEnterScreen
import com.powerly.user.presentation.email.password.reset.EmailPasswordResetScreen
import com.powerly.user.presentation.email.verify.RegisterVerificationScreen
import com.powerly.user.presentation.welcome.WelcomeScreen
import com.powerly.user.presentation.welcome.agreement.UserAgreementDialog
import com.powerly.user.presentation.welcome.language.LanguagesDialog

fun NavGraphBuilder.userDestinations(
    navController: NavHostController
) {

    fun navigateToHome(
        userViewModel: UserViewModel,
        loggedIn: Boolean = false,
        openProfile: Boolean = false
    ) {
        if (loggedIn) {
            userViewModel.cancelRegistrationReminders()
            userViewModel.updateDevice()
        }
        navController.navigate(AppRoutes.Navigation) {
            if (loggedIn) popUpTo(AppRoutes.User) { inclusive = true }
        }
        if (openProfile) navController.navigate(AppRoutes.Account.Profile)
    }

    navigation<AppRoutes.User>(startDestination = AppRoutes.User.Welcome) {
        composable<AppRoutes.User.Welcome> { entry ->
            val userViewModel = entry.sharedGraphViewModel<UserViewModel>(
                navController,
                parentRoute = AppRoutes.User
            )
            val emailViewModel = entry.sharedGraphViewModel<EmailLoginViewModel>(
                navController,
                parentRoute = AppRoutes.User
            )
            WelcomeScreen(
                viewModel = userViewModel,
                navigateToHome = { navigateToHome(userViewModel) },
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

        composable<AppRoutes.User.Email.Login> { entry ->
            val emailViewModel = entry.sharedGraphViewModel<EmailLoginViewModel>(
                navController,
                parentRoute = AppRoutes.User
            )
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

        composable<AppRoutes.User.Email.Password.Create> { entry ->
            val userViewModel = entry.sharedGraphViewModel<UserViewModel>(
                navController,
                parentRoute = AppRoutes.User
            )
            val emailViewModel = entry.sharedGraphViewModel<EmailLoginViewModel>(
                navController,
                parentRoute = AppRoutes.User
            )
            EmailPasswordCreateScreen(
                userViewModel = userViewModel,
                viewModel = emailViewModel,
                navigateToVerification = {
                    navController.navigate(AppRoutes.User.Email.Verification) {
                        popUpTo(AppRoutes.User.Email.Login)
                    }
                },
                onSelectCountry = {
                    navController.navigate(AppRoutes.User.Email.Country)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<AppRoutes.User.Email.Password.Enter> { entry ->
            val userViewModel = entry.sharedGraphViewModel<UserViewModel>(
                navController,
                parentRoute = AppRoutes.User
            )
            val emailViewModel = entry.sharedGraphViewModel<EmailLoginViewModel>(
                navController,
                parentRoute = AppRoutes.User
            )
            EmailPasswordEnterScreen(
                viewModel = emailViewModel,
                navigateToHome = { navigateToHome(userViewModel, loggedIn = true) },
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
        composable<AppRoutes.User.Email.Password.Reset> { entry ->
            val emailViewModel = entry.sharedGraphViewModel<EmailLoginViewModel>(
                navController,
                parentRoute = AppRoutes.User
            )
            EmailPasswordResetScreen(
                viewModel = emailViewModel,
                onBack = { navController.popBackStack() },
            )
        }

        composable<AppRoutes.User.Email.Verification> { entry ->
            val userViewModel = entry.sharedGraphViewModel<UserViewModel>(
                navController,
                parentRoute = AppRoutes.User
            )
            val emailViewModel = entry.sharedGraphViewModel<EmailLoginViewModel>(
                navController,
                parentRoute = AppRoutes.User
            )
            val uriHandler = LocalUriHandler.current
            RegisterVerificationScreen(
                viewModel = emailViewModel,
                callSupport = {
                    val url = "tel:${userViewModel.supportNumber}"
                    uriHandler.openUriSafely(url)
                },
                navigateToHome = { navigateToHome(userViewModel, loggedIn = true) },
                onBack = { navController.popBackStack() }
            )
        }

        composable<AppRoutes.User.Email.Country> { entry ->
            val userViewModel = entry.sharedGraphViewModel<UserViewModel>(
                navController,
                parentRoute = AppRoutes.User
            )
            CountriesDialog(
                onDismiss = { navController.popBackStack() },
                countriesList = { userViewModel.countries },
                selectedCountry = { userViewModel.country.value },
                onSelectCountry = userViewModel::setCountry
            )
        }
    }
}

