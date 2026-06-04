package com.powerly.core.domain.repository

import androidx.paging.PagingData
import com.powerly.core.domain.model.SourceStatus
import com.powerly.core.domain.model.SourcesStatus
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.Connector
import com.powerly.core.model.powerly.Media
import com.powerly.core.model.powerly.Review
import kotlinx.coroutines.flow.Flow


interface PowerSourceRepository {

    suspend fun getNearPowerSources(
        latitude: Double,
        longitude: Double
    ): SourcesStatus

    suspend fun getMedia(id: String): List<Media>

    fun getReviews(id: String): Flow<PagingData<Review>>

    suspend fun getPowerSource(id: String): SourceStatus

    suspend fun connectors(): ApiStatus<List<Connector>>

    suspend fun getPowerSourceByIdentifier(identifier: String): SourceStatus
}
