package com.powerly.payment.domain.repository

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.model.payment.StripCard

interface CardsRepository {

    suspend fun cardList(): ApiStatus<List<StripCard>>

    suspend fun addCard(token: String): ApiStatus<Boolean>

    suspend fun setDefaultCard(id: String): ApiStatus<Boolean>

    suspend fun deleteCard(id: String): ApiStatus<Boolean>
}
