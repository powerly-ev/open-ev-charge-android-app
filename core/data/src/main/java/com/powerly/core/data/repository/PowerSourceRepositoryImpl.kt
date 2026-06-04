package com.powerly.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.powerly.core.data.datasource.remote.PowerSourceRemoteDataSource
import com.powerly.core.domain.repository.PowerSourceRepository
import com.powerly.core.model.api.BasePagingSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single


@Single
internal class PowerSourceRepositoryImpl(
    private val remoteDataSource: PowerSourceRemoteDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : PowerSourceRepository {

    override suspend fun getNearPowerSources(
        latitude: Double,
        longitude: Double
    ) = withContext(ioDispatcher) {
        remoteDataSource.getNearPowerSources(latitude, longitude)
    }

    override suspend fun getMedia(id: String) = withContext(ioDispatcher) {
        remoteDataSource.getMedia(id)
    }

    override fun getReviews(id: String) = Pager(
        config = PagingConfig(pageSize = 15, prefetchDistance = 2),
        pagingSourceFactory = {
            BasePagingSource(
                apiCall = { remoteDataSource.getReviews(id, page = it) }
            )
        }
    ).flow

    override suspend fun getPowerSource(id: String) = withContext(ioDispatcher) {
        remoteDataSource.getPowerSource(id)
    }

    override suspend fun connectors() = withContext(ioDispatcher) {
        remoteDataSource.connectors()
    }

    override suspend fun getPowerSourceByIdentifier(identifier: String) = withContext(ioDispatcher) {
        remoteDataSource.getPowerSourceByIdentifier(identifier)
    }
}
