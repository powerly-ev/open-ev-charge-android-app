package com.powerly.powersource.charging.domain.repository

import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.Session
import com.powerly.powersource.charging.domain.model.ReviewOptionsStatus
import kotlinx.coroutines.flow.Flow

interface FeedbackRepository {

    val reviewOptions: Flow<ReviewOptionsStatus>

    suspend fun reviewAdd(
        orderId: String,
        rating: Double,
        msg: String
    ): ApiStatus<Session>

    suspend fun reviewSkip(orderId: String): ApiStatus<Session>
}
