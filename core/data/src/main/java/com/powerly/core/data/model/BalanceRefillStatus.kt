package com.powerly.core.data.model

import com.powerly.core.model.payment.PaymentRedirect
import com.powerly.core.model.util.Message

sealed class BalanceRefillStatus {
    data object Loading : BalanceRefillStatus()
    data class Error(val msg: Message) : BalanceRefillStatus()
    data class Success(val balance: Double, val msg: Message) : BalanceRefillStatus()
    data class Authenticate(
        val redirect: PaymentRedirect,
        val message: Message
    ) : BalanceRefillStatus()
}