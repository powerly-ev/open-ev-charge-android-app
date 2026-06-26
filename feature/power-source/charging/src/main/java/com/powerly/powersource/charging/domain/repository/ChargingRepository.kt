package com.powerly.powersource.charging.domain.repository

import com.powerly.core.domain.model.ChargingStatus

interface ChargingRepository {

    suspend fun startCharging(
        chargePointId: String,
        quantity: String,
        connector: Int?
    ): ChargingStatus

    suspend fun stopCharging(
        orderId: String,
        chargePointId: String,
        connector: Int?
    ): ChargingStatus

    suspend fun sessionDetails(orderId: String): ChargingStatus
}
