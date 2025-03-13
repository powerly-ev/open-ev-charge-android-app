package com.powerly.payment.balance.withdraw

import androidx.compose.runtime.Composable
import com.powerly.ui.dialogs.MyScreenBottomSheet

@Composable
internal fun WithdrawBalanceDialog(onDismiss: () -> Unit) {
    MyScreenBottomSheet(onDismiss = onDismiss) {
        WithdrawScreen(onClose = onDismiss)
    }
}