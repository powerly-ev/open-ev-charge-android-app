package com.powerly.payment.presentation.wallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.powerly.core.model.payment.StripCard
import com.powerly.payment.presentation.PaymentViewModel
import com.powerly.resources.R
import com.powerly.ui.dialogs.alert.MyAlertDialog
import com.powerly.ui.dialogs.alert.rememberAlertDialogState
import com.powerly.ui.dialogs.loading.rememberScreenState
import com.powerly.ui.dialogs.rememberMyDialogState
import kotlinx.coroutines.launch

@Composable
internal fun WalletScreen(
    sharedViewModel: PaymentViewModel,
    addPaymentMethod: () -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val screenState = rememberScreenState()
    val deleteCardDialog = rememberAlertDialogState()
    val walletOptionsDialog = rememberMyDialogState()
    val paymentMethods = remember { sharedViewModel.paymentMethods }
    var selectedCard by remember { mutableStateOf<StripCard?>(null) }

    fun defaultCard(card: StripCard) {
        coroutineScope.launch {
            screenState.loading = true
            val default = sharedViewModel.setDefaultCard(card)
            screenState.loading = false
            if (default && paymentMethods.isNotEmpty()) screenState.showSuccess()
        }
    }

    fun deleteCard(stripCard: StripCard) {
        coroutineScope.launch {
            screenState.loading = true
            val deleted = sharedViewModel.deleteCard(stripCard.id)
            screenState.loading = false
            if (deleted) screenState.showSuccess()
        }
    }

    LaunchedEffect(Unit) {
        screenState.loading = true
        sharedViewModel.loadPaymentMethods()
        screenState.loading = false
        if (paymentMethods.isEmpty()) addPaymentMethod()
    }

    WalletOptionsDialog(
        card = { selectedCard },
        state = walletOptionsDialog,
        onRemove = { deleteCardDialog.show() },
        onMakeDefault = { defaultCard(selectedCard!!) }
    )

    MyAlertDialog(
        state = deleteCardDialog,
        message = stringResource(R.string.payment_delete_card_message),
        positiveButton = stringResource(R.string.yes),
        negativeButton = stringResource(R.string.no),
        positiveButtonClick = { deleteCard(selectedCard!!) }
    )

    WalletScreenContent(
        screenState = screenState,
        cards = { paymentMethods },
        onAddCard = addPaymentMethod,
        onSelectCard = {
            selectedCard = it
            walletOptionsDialog.show()
        },
        onClose = onBack
    )
}
