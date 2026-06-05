package com.powerly.powersource.charging.data.repository

import com.powerly.core.domain.model.ChargingStatus
import com.powerly.core.domain.model.ApiStatus
import com.powerly.powersource.charging.data.datasource.remote.ChargingRemoteDataSource
import com.powerly.powersource.charging.domain.repository.ChargingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
internal class ChargingRepositoryImpl(
    private val remoteDataSource: ChargingRemoteDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : ChargingRepository {

    override suspend fun startCharging(
        chargePointId: String,
        quantity: String,
        connector: Int?
    ) = withContext(ioDispatcher) {
        remoteDataSource.startCharging(chargePointId, quantity, connector).toChargingStatus()
    }

    override suspend fun stopCharging(
        orderId: String,
        chargePointId: String,
        connector: Int?
    ) = withContext(ioDispatcher) {
        when (val result = remoteDataSource.stopCharging(orderId, chargePointId, connector)) {
            is ApiStatus.Success -> ChargingStatus.Stop(result.data)
            is ApiStatus.Error -> ChargingStatus.Error(result.msg)
            else -> ChargingStatus.Loading
        }
    }

    override suspend fun sessionDetails(orderId: String) = withContext(ioDispatcher) {
        remoteDataSource.sessionDetails(orderId).toChargingStatus()
    }

    private fun ApiStatus<com.powerly.core.model.powerly.Session>.toChargingStatus(): ChargingStatus =
        when (this) {
            is ApiStatus.Success -> ChargingStatus.Success(data)
            is ApiStatus.Error -> ChargingStatus.Error(msg)
            else -> ChargingStatus.Loading
        }
}
