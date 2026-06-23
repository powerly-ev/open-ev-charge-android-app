package com.powerly.orders.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.powerly.core.network.api.BasePagingSource
import com.powerly.orders.data.datasource.remote.SessionsRemoteDataSource
import com.powerly.core.data.model.powerly.toDomain
import com.powerly.orders.domain.repository.SessionsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
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
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }

    override val completedOrders
        get() = Pager(
            config = PagingConfig(pageSize = 15, prefetchDistance = 2),
            pagingSourceFactory = {
                BasePagingSource(
                    apiCall = { remoteDataSource.getCompleteOrders(page = it) }
                )
            }
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }

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
