package com.powerly.payment.presentation.balance.show

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.payment.BalanceItem
import com.powerly.payment.presentation.PaymentViewModel
import kotlinx.coroutines.launch

@Composable
internal fun ShowBalanceScreen(
    viewModel: ShowBalanceViewModel,
    sharedViewModel: PaymentViewModel,
    onAddBalance: (BalanceItem) -> Unit,
    onBack: () -> Unit
) {
    val balanceItems = viewModel.balanceItems.collectAsState(initial = ApiStatus.Loading)
    val coroutineScope = rememberCoroutineScope()
    val currency by remember { sharedViewModel.userCurrency }
    val balance by remember { sharedViewModel.userBalance }

    BackHandler(onBack = onBack)

    ShowBalanceScreenContent(
        balanceState = balanceItems.value,
        currency = currency,
        balance = balance,
        onAddBalance = onAddBalance,
        onRefreshBalance = {
            coroutineScope.launch { viewModel.refreshUser() }
        },
        onClose = onBack
    )
}
