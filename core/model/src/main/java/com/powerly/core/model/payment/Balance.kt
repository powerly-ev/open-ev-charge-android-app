package com.powerly.core.model.payment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BalanceItem(
    @SerialName("id") val id: Int = -1,
    @SerialName("price") val price: Double = 0.0,
    @SerialName("bonus") val bonus: Double = 0.0,
    @SerialName("popular") val popular: Boolean = false
) {
    var currency: String = ""
    val totalBalance: Double get() = price + bonus
    val vat = 0.0
    val active: Boolean get() = true
}

@Serializable
class BalanceRefillBody(
    @SerialName("offer_id") private val offerId: Int,
    @SerialName("payment_method_id") private val paymentMethodId: String
)

@Serializable
data class BalanceRefill(
    @SerialName("new_balance") val newBalance: Double,
    @SerialName("next_action") private val nextAction: NextAction?
) {
    val hasRedirectUrl: Boolean get() = nextAction != null
    val redirectUrl: PaymentRedirect get() = nextAction!!.redirectToUrl
}

@Serializable
data class NextAction(
    @SerialName("redirect_to_url") val redirectToUrl: PaymentRedirect,
    @SerialName("type") val type: String
)

@Serializable
data class PaymentRedirect(
    @SerialName("return_url") val returnUrl: String,
    @SerialName("url") val url: String
)

