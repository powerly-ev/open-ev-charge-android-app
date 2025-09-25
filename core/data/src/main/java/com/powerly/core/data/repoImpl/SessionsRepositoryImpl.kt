package com.powerly.core.data.repoImpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.powerly.core.data.model.ChargingStatus
import com.powerly.core.data.repositories.SessionsRepository
import com.powerly.core.model.api.BasePagingSource
import com.powerly.core.model.powerly.StartChargingBody
import com.powerly.core.model.powerly.StopChargingBody
import com.powerly.core.network.RemoteDataSource
import com.powerly.core.network.asErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class SessionsRepositoryImpl (
    private val remoteDataSource: RemoteDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : SessionsRepository {

    /**
     * Charging
     */

    override suspend fun startCharging(
        chargePointId: String,
        quantity: String,
        connector: Int?
    ) = withContext(ioDispatcher) {
        try {
            val request = StartChargingBody(
                powersourceId = chargePointId,
                quantity = quantity,
                connector = connector
            )
            val response = remoteDataSource.startCharging(request)
            if (response.hasOnlyData) ChargingStatus.Success(response.getData!!)
            else ChargingStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ChargingStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            ChargingStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun stopCharging(
        orderId: String,
        chargePointId: String,
        connector: Int?
    ) = withContext(ioDispatcher) {
        try {
            val request = StopChargingBody(
                powersourceId = chargePointId,
                orderId = orderId,
                connector = connector
            )
            val response = remoteDataSource.stopCharging(request)
            if (response.hasData) ChargingStatus.Stop(response.getData!!)
            else ChargingStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ChargingStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            ChargingStatus.Error(e.asErrorMessage)
        }
    }

    /**
     * Session Details
     */

    override suspend fun sessionDetails(orderId: String) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.sessionDetails(orderId)
            if (response.hasData) ChargingStatus.Success(response.getData!!)
            else ChargingStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ChargingStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            ChargingStatus.Error(e.asErrorMessage)
        }
    }


    override val activeOrders
        get() = Pager(
            config = PagingConfig(pageSize = 15, prefetchDistance = 2),
            pagingSourceFactory = {
                BasePagingSource(
                    apiCall = { remoteDataSource.getActiveOrders(page = it) }
                )
            }
        ).flow


    override val completedOrders
        get() = Pager(
            config = PagingConfig(pageSize = 15, prefetchDistance = 2),
            pagingSourceFactory = {
                BasePagingSource(
                    apiCall = { remoteDataSource.getCompleteOrders(page = it) }
                )
            }
        ).flow

}
