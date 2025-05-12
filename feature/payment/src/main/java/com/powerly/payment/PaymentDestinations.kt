package com.powerly.payment

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.powerly.lib.AppRoutes
import com.powerly.payment.balance.BalanceViewModel
import com.powerly.payment.balance.add.AddBalanceScreen
import com.powerly.payment.balance.show.ShowBalanceScreen
import com.powerly.payment.balance.withdraw.WithdrawBalanceDialog
import com.powerly.payment.methods.PaymentViewModel
import com.powerly.payment.methods.add.AddPaymentDialog
import com.powerly.payment.methods.select.PaymentMethodsDialog
import com.powerly.payment.wallet.WalletScreen

fun NavGraphBuilder.paymentDestinations(
    navController: NavHostController,
    paymentViewModel: PaymentViewModel,
    balanceViewModel: BalanceViewModel,
    onRefreshUser: () -> Unit
) {
    composable<AppRoutes.Payment.Wallet> {
        WalletScreen(
            viewModel = paymentViewModel,
            onBack = { navController.popBackStack() },
            addPaymentMethod = { navController.navigate(AppRoutes.Payment.Add) }
        )
    }

    composable<AppRoutes.Payment.Balance.Show> {
        ShowBalanceScreen(
            viewModel = balanceViewModel,
            onAddBalance = {
                balanceViewModel.setBalanceItem(it)
                navController.navigate(AppRoutes.Payment.Balance.Add)
            },
            onBack = {
                onRefreshUser()
                navController.popBackStack()
            }
        )
    }

    dialog<AppRoutes.Payment.Balance.Withdraw> {
        WithdrawBalanceDialog(onDismiss = { navController.popBackStack() })
    }

    composable<AppRoutes.Payment.Balance.Add> {
        AddBalanceScreen(
            viewModel = balanceViewModel,
            paymentViewModel = paymentViewModel,
            selectPaymentMethod = { navController.navigate(AppRoutes.Payment.Methods) },
            addPaymentMethod = { navController.navigate(AppRoutes.Payment.Add) },
            onBack = { navController.popBackStack() }
        )
    }

    dialog<AppRoutes.Payment.Add> {
        AddPaymentDialog(
            viewModel = paymentViewModel,
            onDismiss = { navController.popBackStack() }
        )
    }

    dialog<AppRoutes.Payment.Methods> {
        PaymentMethodsDialog(
            viewModel = paymentViewModel,
            onAddPaymentMethod = { navController.navigate(AppRoutes.Payment.Add) },
            onDismiss = { navController.popBackStack() }
        )
    }
}
