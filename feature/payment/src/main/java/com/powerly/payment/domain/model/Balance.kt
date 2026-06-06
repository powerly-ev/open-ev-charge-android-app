package com.powerly.payment.domain.model

data class BalanceItem(
    val id: Int = -1,
    val price: Double = 0.0,
    val bonus: Double = 0.0,
    val popular: Boolean = false
) {
    var currency: String = ""
    val totalBalance: Double get() = price + bonus
    val vat = 0.0
    val active: Boolean get() = true
}
