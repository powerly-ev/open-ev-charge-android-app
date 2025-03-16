package com.powerly.core.data.repositories

import androidx.paging.PagingData
import com.powerly.core.data.model.ChargingStatus
import com.powerly.core.model.powerly.Session
import kotlinx.coroutines.flow.Flow

interface SessionsRepository {

    /**
     * Starts charging.
     *
     * @param chargePointId The ID of the charge point.
     * @param quantity The charging quantity.
     * @param connector The connector number.
     * @return  [com.powerly.core.data.model.ChargingStatus] results.
     */
    suspend fun startCharging(
        chargePointId: String,
        quantity: String,
        connector: Int?
    ): ChargingStatus

    /**
     * Stops charging.
     *
     * @param orderId The ID of the charging session.
     * @param chargePointId The ID of the charge point.
     * @param connector The connector number.
     * @return  [ChargingStatus] results.
     */
    suspend fun stopCharging(
        orderId: String,
        chargePointId: String,
        connector: Int?
    ): ChargingStatus

    /**
     * Retrieves session details.
     *
     * @param orderId The ID of the order.
     * @return  [ChargingStatus] results.
     */
    suspend fun sessionDetails(orderId: String): ChargingStatus

    /**
     * Retrieves active sessions (paginated).
     *
     * @return  a paginated list of sessions.
     */
    val activeOrders: Flow<PagingData<Session>>

    /**
     * Retrieves completed sessions (paginated).
     *
     * @return  a paginated list of sessions.
     */
    val completedOrders: Flow<PagingData<Session>>

}







