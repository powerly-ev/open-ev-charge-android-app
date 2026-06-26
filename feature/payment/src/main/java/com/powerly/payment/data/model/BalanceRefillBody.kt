package com.powerly.payment.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class BalanceRefillBody(
    @SerialName("offer_id") private val offerId: Int,
    @SerialName("payment_method_id") private val paymentMethodId: String
)
