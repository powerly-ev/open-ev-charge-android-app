package com.powerly.core.model.payment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Wallet(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("balance") val balance: Double,
    @SerialName("currency") var currency: String = "",
    @SerialName("type") val type: String = "",
    @SerialName("payable") val payable: Boolean = false,
    @SerialName("rechargeable") val rechargeable: Boolean = false,
    @SerialName("withdrawable") val withdrawable: Boolean = false
)