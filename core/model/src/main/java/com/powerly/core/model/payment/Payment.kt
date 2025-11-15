package com.powerly.core.model.payment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StripCard(
    @SerialName("id") val id: String,
    @SerialName("card_number") val cardNumber: String?,
    @SerialName("payment_option") val paymentOption: String,
    @SerialName("default") val default: Boolean
) {
    val paymentType: Int get() = 1
    val isCard: Boolean get() = true
    val isBalance: Boolean get() = false
    val isMada: Boolean get() = false
    val balance: String get() = "0.0"
    val cardNumberHidden: String get() = "************$cardNumber"

    companion object {
        const val CASH_ON_DELIVERY = 0
        const val PAYMENT_METHOD_CARD = 1
        const val POINT_CHECKOUT = 2
        const val PAYMENT_METHOD_EFAWATEER = 3
        const val PAYPAL = 4
        const val APPLE_PAY = 5
        const val PAYMENT_METHOD_BALANCE = 6
        const val PAYMENT_FAIL = "PAYMENT_FAIL"
        const val PAYFORT_DATA = "PAYFORT_DATA"
        const val POINT_URL = "POINT_URL"
        const val PAYMENT_MADA = "MADA"
        const val PAYMENT_STATUS = "paymentStatus"
    }
}

@Serializable
data class AddCardBody(val token: String)
