package com.powerly.payment.presentation.balance.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.powerly.core.model.util.asErrorMessage
import com.powerly.payment.presentation.PaymentViewModel
import com.powerly.resources.R
import com.powerly.ui.dialogs.alert.AlertDialogProperties
import com.powerly.ui.dialogs.alert.MyAlertDialog
import com.powerly.ui.dialogs.loading.rememberScreenState
import kotlinx.coroutines.launch

@Composable
internal fun AddBalanceScreen(
    viewModel: AddBalanceViewModel,
    sharedViewModel: PaymentViewModel,
    addPaymentMethod: () -> Unit,
    selectPaymentMethod: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val screenState = rememberScreenState()
    val currency by remember { sharedViewModel.userCurrency }
    val balanceItem by remember { sharedViewModel.balanceItem }
    var defaultPaymentMethod by remember { sharedViewModel.defaultPaymentMethod }
    val paymentFailureDialog = remember { viewModel.paymentFailureDialog }

    fun refillBalance() {
        if (defaultPaymentMethod == null) {
            val message = context.getString(
                R.string.balance_refill_select_method
            ).asErrorMessage
            screenState.showMessage(message)
            return
        }
        coroutineScope.launch {
            screenState.loading = true
            val refilled = viewModel.refillBalance(
                offerId = balanceItem.id,
                stripCard = defaultPaymentMethod!!
            )
            screenState.loading = false
            if (refilled) screenState.showSuccess { onBack() }
        }
    }

    LaunchedEffect(Unit) {
        sharedViewModel.loadPaymentMethods()
        if (sharedViewModel.paymentMethods.isEmpty()) addPaymentMethod()
    }

    MyAlertDialog(
        state = paymentFailureDialog,
        title = stringResource(R.string.payment_failed),
        positiveButtonClick = {},
        properties = AlertDialogProperties(
            type = AlertDialogProperties.MODAL_DIALOG
        )
    )

    AddBalanceScreenContent(
        screenState = screenState,
        balanceItem = balanceItem.apply { this.currency = currency },
        paymentMethod = { defaultPaymentMethod },
        onAddBalance = ::refillBalance,
        onSelectMethod = selectPaymentMethod,
        onClose = onBack
    )
}
