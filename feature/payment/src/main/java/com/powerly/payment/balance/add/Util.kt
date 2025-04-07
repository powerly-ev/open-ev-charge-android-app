package com.powerly.payment.balance.add

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.powerly.core.model.payment.StripCard
import com.powerly.resources.R

@Composable
internal fun paymentMethodDetails(
    card: StripCard?,
    shortTitle: Boolean = true
): Pair<String, Int> {
    val title: String
    val icon: Int
    when (card?.paymentType) {
        StripCard.CASH_ON_DELIVERY -> {
            title = stringResource(R.string.payment_cash_on_delivery)
            icon = R.drawable.ic_payment_cash_logo
        }

        StripCard.PAYMENT_METHOD_CARD -> {
            title = if (shortTitle) card.paymentOption
            else "${card.paymentOption} ${card.cardNumber}"
            icon = if (card.isMada) R.drawable.mada_card
            else R.drawable.icon_card
        }

        StripCard.PAYPAL -> {
            title = stringResource(R.string.payment_paypal)
            icon = R.drawable.paypal
        }

        StripCard.PAYMENT_METHOD_BALANCE -> {
            title = stringResource(R.string.balance)
            icon = R.drawable.ic_payment_balance
        }

        else -> {
            title = stringResource(R.string.order_payment_method)
            icon = R.drawable.ic_payment_cash_logo
        }
    }
    return Pair(title, icon)
}