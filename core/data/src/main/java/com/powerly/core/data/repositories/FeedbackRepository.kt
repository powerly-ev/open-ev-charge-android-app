package com.powerly.core.data.repositories

import com.powerly.core.data.model.ReviewOptionsStatus
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.Session
import kotlinx.coroutines.flow.Flow

interface FeedbackRepository {

    /**
     * Retrieves feedback reasons.
     *
     * @return  [ReviewOptionsStatus] results containing the feedback reasons.
     */
    val reviewOptions: Flow<ReviewOptionsStatus>

    /**
     * Adds feedback.
     *
     * @param orderId The ID of the order.
     * @param rating The rating given.
     * @param msg The feedback message.
     * @return  [ApiStatus] results indicating the success or failure of adding feedback.
     */
    suspend fun reviewAdd(
        orderId: String,
        rating: Double,
        msg: String
    ): ApiStatus<Session>

    /**
     * Skips feedback for an order.
     *
     * @param orderId The ID of the order.
     * @return  [ApiStatus] results indicating the success or failure of skipping feedback.
     */
    suspend fun reviewSkip(orderId: String): ApiStatus<Session>

    /**
     * Retrieves pending feedback.
     *
     * @return  [ApiStatus] results containing pending feedback, or an error status.
     */
    suspend fun reviewsList(): ApiStatus<Session?>
}
