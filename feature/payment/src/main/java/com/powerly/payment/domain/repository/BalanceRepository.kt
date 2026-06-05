package com.powerly.payment.domain.repository

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.model.payment.BalanceItem
import com.powerly.core.model.payment.Wallet
import com.powerly.core.domain.model.Message
import com.powerly.payment.domain.model.BalanceRefillStatus

interface BalanceRepository {

    /**
     * Refills the balance.
     *
     * @param offerId ID of the balance offer selected by the user.
     * @param paymentMethodId method ID used for the transaction.
     */
    suspend fun refillBalance(offerId: Int, paymentMethodId: String): BalanceRefillStatus

    suspend fun getBalanceItems(countryId: Int?): ApiStatus<List<BalanceItem>>

    suspend fun walletList(): ApiStatus<List<Wallet>>

    suspend fun walletPayout(): ApiStatus<Message>
}
