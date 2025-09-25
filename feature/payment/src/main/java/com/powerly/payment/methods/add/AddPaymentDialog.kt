package com.powerly.payment.methods.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.powerly.payment.methods.PaymentViewModel
import com.powerly.ui.dialogs.MyScreenBottomSheet

private const val TAG = "AddPaymentScreen"

@Composable
internal fun AddPaymentDialog(
    viewModel: PaymentViewModel = koinViewModel(),
    onDismiss: () -> Unit
) {
    val screenState = remember { viewModel.screenState }

    MyScreenBottomSheet(onDismiss = onDismiss) {
        AddPaymentScreenContent(
            screenState = screenState,
            cardNumber = viewModel.scannedCard,
            onAddCard = { parms ->
                viewModel.addCard(parms) {
                    onDismiss()
                }
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