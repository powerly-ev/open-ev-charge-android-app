package com.powerly.payment.domain.model

data class Wallet(
    val id: Int,
    val name: String,
    val balance: Double,
    var currency: String = "",
    val type: String = "",
    val payable: Boolean = false,
    val rechargeable: Boolean = false,
    val withdrawable: Boolean = false
)
