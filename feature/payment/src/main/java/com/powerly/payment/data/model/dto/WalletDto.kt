package com.powerly.payment.data.model.dto

import com.powerly.payment.domain.model.Wallet
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class WalletDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("balance") val balance: Double,
    @SerialName("currency") val currency: String = "",
    @SerialName("type") val type: String = "",
    @SerialName("payable") val payable: Boolean = false,
    @SerialName("rechargeable") val rechargeable: Boolean = false,
    @SerialName("withdrawable") val withdrawable: Boolean = false
)

internal fun WalletDto.toDomain(): Wallet = Wallet(
    id = id,
    name = name,
    balance = balance,
    currency = currency,
    type = type,
    payable = payable,
    rechargeable = rechargeable,
    withdrawable = withdrawable
)
