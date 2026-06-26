package com.powerly.payment.data.model.dto

import com.powerly.payment.domain.model.BalanceItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BalanceItemDto(
    @SerialName("id") val id: Int = -1,
    @SerialName("price") val price: Double = 0.0,
    @SerialName("bonus") val bonus: Double = 0.0,
    @SerialName("popular") val popular: Boolean = false
)

internal fun BalanceItemDto.toDomain(): BalanceItem = BalanceItem(
    id = id,
    price = price,
    bonus = bonus,
    popular = popular
)
