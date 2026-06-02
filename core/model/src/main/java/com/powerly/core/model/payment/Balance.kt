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
