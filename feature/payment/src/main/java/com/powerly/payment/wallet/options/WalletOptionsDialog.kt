package com.powerly.payment.wallet.options

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.powerly.core.model.payment.StripCard
import com.powerly.core.model.util.Item
import com.powerly.resources.R
import com.powerly.ui.dialogs.MyDialogState


@Composable
internal fun WalletOptionsDialog(
    state: MyDialogState,
    card: () -> StripCard?,
    onRemove: () -> Unit,
    onMakeDefault: () -> Unit,
) {
    val context = LocalContext.current
    val title = context.getString(R.string.payment_card)
    val options = remember { mutableStateListOf<Item>() }

    fun initCard(card: StripCard) {
        options.clear()
        val paymentType = card.paymentType
        if (paymentType == -1) return

        val defaultItem = Item(
            context.getString(R.string.payment_make_default),
            selected = card.default,
            id = "0"
        )
        when (paymentType) {
            StripCard.PAYMENT_METHOD_CARD -> {
                options.add(defaultItem)
                options.add(Item(context.getString(R.string.remove), id = "2"))
            }

            StripCard.PAYMENT_METHOD_BALANCE -> {
                options.add(defaultItem)
                options.add(Item(context.getString(R.string.balance_add), id = "3"))
            }

            else -> options.add(defaultItem)
        }
    }

    LaunchedEffect(card()) {
        card()?.let { initCard(it) }
    }

    WalletOptionsDialogContent(
        title = title,
        options = options,
        onSelect = {
            if (it.id == "0") onMakeDefault()
            else if (it.id == "2") onRemove()
            state.dismiss()
        },
        state = state
    )
}