package com.powerly.core.data.repositories

import com.powerly.core.data.model.BalanceRefillStatus
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.payment.BalanceItem
import com.powerly.core.model.payment.StripCard
import com.powerly.core.model.payment.Wallet
import com.powerly.core.model.util.Message

interface PaymentRepository {

    /**
     * Retrieves a list of cards.
     *
     * @return  [ApiStatus] results containing the list of cards.
     */
    suspend fun cardList(): ApiStatus<List<StripCard>>

    /**
     * Adds a card.
     *
     * @param token The card token.
     * @return  [ApiStatus] results indicating the success or failure of adding the card.
     */
    suspend fun addCard(token: String): ApiStatus<Boolean>

    /**
     * Sets a card as the default.
     *
     * @param id The ID of the card to set as default.
     * @return  [ApiStatus] results indicating the success or failure of setting the default card.
     */
    suspend fun setDefaultCard(id: String): ApiStatus<Boolean>

    /**
     * Deletes a card.
     *
     * @param id The ID of the card to delete.
     * @return  [ApiStatus] results indicating the success or failure of deleting the card.
     */
    suspend fun deleteCard(id: String): ApiStatus<Boolean>

    /**
     * Refills the balance.
     *
     * @param offerId ID of the balance offer selected by the user.
     * @param paymentMethodId method ID used for the transaction.
     * @return  [com.powerly.core.data.model.BalanceRefillStatus] results indicating the status of the refill.
     */
    suspend fun refillBalance(offerId: Int, paymentMethodId: String): BalanceRefillStatus

    /**
     * Retrieves balance items.
     *
     * @param countryId The ID of the country.
     * @return  [ApiStatus] results containing the balance items.
     */
    suspend fun getBalanceItems(countryId: Int?): ApiStatus<List<BalanceItem>>

    /**
     * Retrieves a list of wallets.
     * @return  [ApiStatus] results containing wallet data.
     */
    suspend fun walletList(): ApiStatus<List<Wallet>>

    /**
     * Initiates a wallet payout.
     * @return  [ApiStatus] results indicating payout status.
     */
    suspend fun walletPayout(): ApiStatus<Message>
}


