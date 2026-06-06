package com.powerly.payment.presentation.paymentMethods.select

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.powerly.payment.domain.model.StripCard
import com.powerly.payment.presentation.PaymentViewModel
import com.powerly.ui.dialogs.MyScreenBottomSheet
import kotlinx.coroutines.launch

@Composable
internal fun PaymentMethodsDialog(
    sharedViewModel: PaymentViewModel,
    paymentPurpose: PaymentPurpose = PaymentPurpose.SELECT_DEFAULT,
    onAddPaymentMethod: () -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val screenState = remember { sharedViewModel.screenState }
    val paymentMethods = remember { sharedViewModel.paymentMethods }
    var selectedCard by remember { mutableStateOf<StripCard?>(null) }

    fun saveDefaultMethod(card: StripCard) {
        selectedCard = card
        coroutineScope.launch {
            screenState.loading = true
            val default = sharedViewModel.setDefaultCard(card)
            screenState.loading = false
            if (default) onDismiss()
        }
    }

    fun onSelectMethod(method: StripCard) {
        if (paymentPurpose == PaymentPurpose.SELECT_DEFAULT) {
            saveDefaultMethod(method)
        } else {
            selectedCard = method
            onDismiss()
        }
    }

    LaunchedEffect(Unit) {
        screenState.loading = true
        sharedViewModel.loadPaymentMethods()
        screenState.loading = false
    }

    MyScreenBottomSheet(onDismiss = onDismiss) {
        PaymentMethodsScreenContent(
            screenState = screenState,
            methods = { paymentMethods },
            onAdd = onAddPaymentMethod,
            onSelect = ::onSelectMethod,
            onClose = onDismiss
        )
    }
}

enum class PaymentPurpose {
    SELECT_DEFAULT,
    SELECT_CARD,
}
