package com.powerly.payment.domain.model

import com.powerly.core.model.util.Message

sealed class BalanceRefillStatus {
    data object Loading : BalanceRefillStatus()
    data class Error(val msg: Message) : BalanceRefillStatus()
    data class Success(val balance: Double, val msg: Message) : BalanceRefillStatus()
    data class Authenticate(
        val redirectUrl: String,
        val message: Message,
        val balance: Double
    ) : BalanceRefillStatus()
}
