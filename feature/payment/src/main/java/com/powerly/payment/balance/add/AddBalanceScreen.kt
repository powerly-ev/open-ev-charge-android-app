package com.powerly.payment.balance.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.powerly.core.model.util.asErrorMessage
import com.powerly.payment.balance.BalanceViewModel
import com.powerly.payment.methods.PaymentViewModel
import com.powerly.resources.R
import com.powerly.ui.dialogs.alert.AlertDialogProperties
import com.powerly.ui.dialogs.alert.MyAlertDialog
import com.powerly.ui.dialogs.loading.rememberScreenState
import kotlinx.coroutines.launch

private const val TAG = "AddBalanceScreen"

@Composable
internal fun AddBalanceScreen(
    viewModel: BalanceViewModel,
    paymentViewModel: PaymentViewModel,
    addPaymentMethod: () -> Unit,
    selectPaymentMethod: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val screenState = rememberScreenState()
    val currency = remember { viewModel.userCurrency }
    val balanceItem by remember { viewModel.balanceItem }
    var defaultPaymentMethod by remember { paymentViewModel.defaultPaymentMethod }
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
            val refilled = viewModel.refillBalance(defaultPaymentMethod!!)
            if (refilled) {
                viewModel.updateUserDetails()
                screenState.loading = false
                screenState.showSuccess {
                    onBack()
                }
            } else {
                screenState.loading = false
            }
        }
    }


    LaunchedEffect(Unit) {
        paymentViewModel.loadPaymentMethods()
        if (paymentViewModel.paymentMethods.isEmpty()) addPaymentMethod()
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