package com.powerly.payment.balance.show

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.payment.BalanceItem
import com.powerly.payment.balance.BalanceViewModel

private const val TAG = "ShowBalanceScreen"

@Composable
internal fun ShowBalanceScreen(
    viewModel: BalanceViewModel,
    onAddBalance: (BalanceItem) -> Unit,
    onBack: () -> Unit
) {
    val balanceItems = viewModel.balanceItems.collectAsState(initial = ApiStatus.Loading)
    val currency = remember { viewModel.userCurrency }
    val balance = remember { viewModel.userBalance }

    BackHandler(onBack = onBack)

    ShowBalanceScreenContent(
        balanceState = balanceItems.value,
        currency = currency,
        balance = balance,
        onAddBalance = onAddBalance,
        onClose = onBack
    )
}