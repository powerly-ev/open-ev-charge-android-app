package com.powerly.payment.data.model.dto

import com.powerly.payment.domain.model.StripCard
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class StripCardDto(
    @SerialName("id") val id: String,
    @SerialName("card_number") val cardNumber: String?,
    @SerialName("payment_option") val paymentOption: String,
    @SerialName("default") val default: Boolean
)

internal fun StripCardDto.toDomain(): StripCard = StripCard(
    id = id,
    cardNumber = cardNumber,
    paymentOption = paymentOption,
    default = default
)
