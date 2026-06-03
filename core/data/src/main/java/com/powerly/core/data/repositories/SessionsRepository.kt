package com.powerly.core.data.repositories

import androidx.paging.PagingData
import com.powerly.core.data.model.ChargingStatus
import com.powerly.core.model.powerly.Session
import kotlinx.coroutines.flow.Flow

interface SessionsRepository {

    /**
     * Stops a charging session.
     *
     * @param orderId The ID of the charging session.
     * @param chargePointId The ID of the charge point.
     * @param connector The connector number.
     */
    suspend fun stopCharging(
        orderId: String,
        chargePointId: String,
        connector: Int?
    ): ChargingStatus

    /** A paginated stream of the user's active sessions. */
    val activeOrders: Flow<PagingData<Session>>

    /** A paginated stream of the user's completed sessions. */
    val completedOrders: Flow<PagingData<Session>>
}
