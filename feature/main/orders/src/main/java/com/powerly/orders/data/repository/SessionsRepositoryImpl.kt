package com.powerly.orders.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.powerly.core.model.api.BasePagingSource
import com.powerly.orders.data.datasource.remote.SessionsRemoteDataSource
import com.powerly.orders.domain.repository.SessionsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
internal class SessionsRepositoryImpl(
    private val remoteDataSource: SessionsRemoteDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : SessionsRepository {

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

    override suspend fun stopCharging(
        orderId: String,
        chargePointId: String,
        connector: Int?
    ) = withContext(ioDispatcher) {
        remoteDataSource.stopCharging(
            orderId = orderId,
            chargePointId = chargePointId,
            connector = connector
        )
    }
}
