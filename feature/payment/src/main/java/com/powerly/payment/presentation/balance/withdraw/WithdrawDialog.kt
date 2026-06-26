package com.powerly.payment.presentation.balance.withdraw

import androidx.compose.runtime.Composable
import com.powerly.ui.dialogs.MyScreenBottomSheet

@Composable
internal fun WithdrawBalanceDialog(
    viewModel: WithdrawViewModel,
    onDismiss: () -> Unit
) {
    MyScreenBottomSheet(onDismiss = onDismiss) {
        WithdrawScreen(viewModel = viewModel, onClose = onDismiss)
    }
}
