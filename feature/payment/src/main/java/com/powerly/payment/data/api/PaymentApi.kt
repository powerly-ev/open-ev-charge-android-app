package com.powerly.payment.data.api

internal object PaymentApi {
    const val PAYMENT_CARDS = "payment-cards"
    const val PAYMENT_CARD_DEFAULT = "payment-cards/default/{id}"
    const val PAYMENT_CARD_DELETE = "payment-cards/{id}"

    const val BALANCE_OFFERS = "balance/offers"
    const val BALANCE_REFILL = "balance/top-up"

    const val PAYOUTS = "payouts"
    const val PAYOUTS_REQUEST = "payouts/request-payout"

    fun cardDefault(id: String): String = PAYMENT_CARD_DEFAULT.replace("{id}", id)
    fun cardDelete(id: String): String = PAYMENT_CARD_DELETE.replace("{id}", id)
}
