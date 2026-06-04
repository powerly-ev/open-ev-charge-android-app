package com.powerly.core.data.datasource.remote

import com.powerly.core.data.api.PowerSourceApi
import com.powerly.core.domain.model.SourceStatus
import com.powerly.core.domain.model.SourcesStatus
import com.powerly.core.model.api.ApiResponse
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.api.BaseResponsePaginated
import com.powerly.core.model.powerly.Connector
import com.powerly.core.model.powerly.Media
import com.powerly.core.model.powerly.PowerSource
import com.powerly.core.model.powerly.Review
import com.powerly.core.network.KtorClient
import com.powerly.core.network.asErrorMessage
import com.powerly.core.network.safeApiCall
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.koin.core.annotation.Single

@Single
internal class PowerSourceRemoteDataSource(
    ktorClient: KtorClient,
) {
    private val client = ktorClient.client

    suspend fun getNearPowerSources(
        latitude: Double,
        longitude: Double,
        search: String? = null
    ): SourcesStatus = try {
        val response: BaseResponsePaginated<PowerSource> = client.get(PowerSourceApi.SOURCES) {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            if (search != null) parameter("search", search)
        }.body()
        if (response.hasData) SourcesStatus.Success(response.data.orEmpty())
        else SourcesStatus.Error(response.getMessage())
    } catch (e: ResponseException) {
        e.printStackTrace()
        SourcesStatus.Error(e.asErrorMessage())
    } catch (e: Exception) {
        e.printStackTrace()
        SourcesStatus.Error(e.asErrorMessage())
    }

    suspend fun getMedia(id: String): List<Media> {
        val result = safeApiCall<List<Media>> {
            val endpoint = PowerSourceApi.MEDIA.replace("{id}", id)
            client.get(endpoint).body<ApiResponse<List<Media>?>>()
        }
        return (result as? ApiStatus.Success)?.data.orEmpty()
    }

    suspend fun getReviews(id: String, page: Int, limit: Int = 15): BaseResponsePaginated<Review> {
        val endpoint = PowerSourceApi.REVIEWS.replace("{id}", id)
        return client.get(endpoint) {
            parameter("page", page)
            parameter("limit", limit)
        }.body()
    }

    suspend fun getPowerSource(id: String): SourceStatus {
        val result = safeApiCall<PowerSource> {
            val endpoint = PowerSourceApi.SOURCE.replace("{id}", id)
            client.get(endpoint).body<ApiResponse<PowerSource?>>()
        }
        return when (result) {
            is ApiStatus.Success -> SourceStatus.Success(result.data)
            is ApiStatus.Error -> SourceStatus.Error(result.msg)
            is ApiStatus.Loading -> SourceStatus.Loading
        }
    }

    suspend fun getPowerSourceByIdentifier(identifier: String): SourceStatus {
        val result = safeApiCall<PowerSource> {
            val endpoint = PowerSourceApi.BY_IDENTIFIER.replace("{identifier}", identifier)
            client.get(endpoint).body<ApiResponse<PowerSource?>>()
        }
        return when (result) {
            is ApiStatus.Success -> SourceStatus.Success(result.data)
            is ApiStatus.Error -> SourceStatus.Error(result.msg)
            is ApiStatus.Loading -> SourceStatus.Loading
        }
    }

    suspend fun connectors(): ApiStatus<List<Connector>> = safeApiCall {
        client.get(PowerSourceApi.CONNECTORS).body<ApiResponse<List<Connector>?>>()
    }
}
