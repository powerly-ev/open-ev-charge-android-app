package com.powerly.payment.presentation.paymentMethods.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.powerly.payment.presentation.PaymentViewModel
import com.powerly.ui.dialogs.MyScreenBottomSheet

@Composable
internal fun AddPaymentDialog(
    viewModel: AddPaymentViewModel,
    sharedViewModel: PaymentViewModel,
    onDismiss: () -> Unit
) {
    val screenState = remember { viewModel.screenState }

    MyScreenBottomSheet(onDismiss = onDismiss) {
        AddPaymentScreenContent(
            screenState = screenState,
            cardNumber = viewModel.scannedCard,
            onAddCard = { parms ->
                viewModel.addCard(
                    parms = parms,
                    onAdded = { sharedViewModel.loadPaymentMethods(forceUpdate = true) },
                    onDismiss = onDismiss
                )
            },
            onScanCard = { viewModel.scanCard() },
            onClose = onDismiss
        )
    }

    DisposableEffect(LocalLifecycleOwner) {
        onDispose {
            viewModel.clearScannedCard()
        }
    }
}
