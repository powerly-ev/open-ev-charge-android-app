package com.powerly.payment.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BalanceRefillResponse(
    @SerialName("new_balance") val newBalance: Double,
    @SerialName("next_action") private val nextAction: NextAction?
) {
    val redirectUrl: String? get() = nextAction?.redirectToUrl?.url
}

@Serializable
internal data class NextAction(
    @SerialName("redirect_to_url") val redirectToUrl: PaymentRedirect,
    @SerialName("type") val type: String
)

@Serializable
internal data class PaymentRedirect(
    @SerialName("return_url") val returnUrl: String,
    @SerialName("url") val url: String
)
