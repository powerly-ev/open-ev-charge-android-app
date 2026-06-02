package com.powerly.payment.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import com.powerly.lib.AppRoutes
import com.powerly.payment.presentation.balance.add.AddBalanceScreen
import com.powerly.payment.presentation.balance.show.ShowBalanceScreen
import com.powerly.payment.presentation.balance.withdraw.WithdrawBalanceDialog
import com.powerly.payment.presentation.paymentMethods.add.AddPaymentDialog
import com.powerly.payment.presentation.paymentMethods.select.PaymentMethodsDialog
import com.powerly.payment.presentation.wallet.WalletScreen
import com.powerly.ui.navigation.sharedGraphViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.paymentDestinations(
    navController: NavHostController
) {
    navigation<AppRoutes.Payment>(startDestination = AppRoutes.Payment.Wallet) {
        composable<AppRoutes.Payment.Wallet> { entry ->
            val sharedViewModel = entry.sharedGraphViewModel<PaymentViewModel>(
                navController,
                parentRoute = AppRoutes.Payment
            )
            WalletScreen(
                sharedViewModel = sharedViewModel,
                onBack = { navController.popBackStack() },
                addPaymentMethod = { navController.navigate(AppRoutes.Payment.Add) }
            )
        }

        composable<AppRoutes.Payment.Balance.Show> { entry ->
            val sharedViewModel = entry.sharedGraphViewModel<PaymentViewModel>(
                navController,
                parentRoute = AppRoutes.Payment
            )
            ShowBalanceScreen(
                viewModel = koinViewModel(),
                sharedViewModel = sharedViewModel,
                onAddBalance = {
                    sharedViewModel.setBalanceItem(it)
                    navController.navigate(AppRoutes.Payment.Balance.Add)
                },
                onBack = { navController.popBackStack() }
            )
        }

        dialog<AppRoutes.Payment.Balance.Withdraw> {
            WithdrawBalanceDialog(
                viewModel = koinViewModel(),
                onDismiss = { navController.popBackStack() }
            )
        }

        composable<AppRoutes.Payment.Balance.Add> { entry ->
            val sharedViewModel = entry.sharedGraphViewModel<PaymentViewModel>(
                navController,
                parentRoute = AppRoutes.Payment
            )
            AddBalanceScreen(
                viewModel = koinViewModel(),
                sharedViewModel = sharedViewModel,
                selectPaymentMethod = { navController.navigate(AppRoutes.Payment.Methods) },
                addPaymentMethod = { navController.navigate(AppRoutes.Payment.Add) },
                onBack = { navController.popBackStack() }
            )
        }

        dialog<AppRoutes.Payment.Add> { entry ->
            val sharedViewModel = entry.sharedGraphViewModel<PaymentViewModel>(
                navController,
                parentRoute = AppRoutes.Payment
            )
            AddPaymentDialog(
                viewModel = koinViewModel(),
                sharedViewModel = sharedViewModel,
                onDismiss = { navController.popBackStack() }
            )
        }

        dialog<AppRoutes.Payment.Methods> { entry ->
            val sharedViewModel = entry.sharedGraphViewModel<PaymentViewModel>(
                navController,
                parentRoute = AppRoutes.Payment
            )
            PaymentMethodsDialog(
                sharedViewModel = sharedViewModel,
                onAddPaymentMethod = { navController.navigate(AppRoutes.Payment.Add) },
                onDismiss = { navController.popBackStack() }
            )
        }
    }
}
