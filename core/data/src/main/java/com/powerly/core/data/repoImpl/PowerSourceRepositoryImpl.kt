package com.powerly.core.data.repoImpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.powerly.core.data.model.SourceStatus
import com.powerly.core.data.model.SourcesStatus
import com.powerly.core.data.repositories.PowerSourceRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.api.BasePagingSource
import com.powerly.core.model.powerly.Media
import com.powerly.core.network.RemoteDataSource
import com.powerly.core.network.asErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import retrofit2.HttpException


@Single
class PowerSourceRepositoryImpl (
    private val remoteDataSource: RemoteDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : PowerSourceRepository {

    override suspend fun getNearPowerSources(
        latitude: Double,
        longitude: Double
    ) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.getNearPowerSources(latitude, longitude, null)
            if (response.hasData) SourcesStatus.Success(response.data.orEmpty())
            else SourcesStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            e.printStackTrace()
            SourcesStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            SourcesStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun getMedia(id: String): List<Media> = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.getMedia(id)
            if (response.hasData) response.getData.orEmpty()
            else emptyList<Media>()
        } catch (e: HttpException) {
            e.printStackTrace()
            emptyList<Media>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList<Media>()
        }
    }

    override fun getReviews(id: String) = Pager(
        config = PagingConfig(pageSize = 15, prefetchDistance = 2),
        pagingSourceFactory = {
            BasePagingSource(
                apiCall = { remoteDataSource.getReviews(id, page = it) }
            )
        }
    ).flow


    override suspend fun getPowerSource(id: String) =
        withContext(ioDispatcher) {
            try {
                val response = remoteDataSource.getPowerSource(id = id)
                if (response.hasData) SourceStatus.Success(response.getData!!)
                else SourceStatus.Error(response.getMessage())
            } catch (e: HttpException) {
                SourceStatus.Error(e.asErrorMessage)
            } catch (e: Exception) {
                SourceStatus.Error(e.asErrorMessage)
            }
        }


    override suspend fun connectors() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.vehiclesConnectors()
            if (response.hasData) ApiStatus.Success(response.getData.orEmpty())
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage)
        }
    }


    override suspend fun getPowerSourceByIdentifier(identifier: String) =
        withContext(ioDispatcher) {
            try {
                val response = remoteDataSource.powerSourceDetails(identifier)
                if (response.hasData) SourceStatus.Success(response.getData!!)
                else SourceStatus.Error(response.getMessage())
            } catch (e: HttpException) {
                e.printStackTrace()
                SourceStatus.Error(e.asErrorMessage)
            } catch (e: Exception) {
                e.printStackTrace()
                SourceStatus.Error(e.asErrorMessage)
            }
        }

}