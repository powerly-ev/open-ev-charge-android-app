package com.powerly.core.data.repositories

import androidx.paging.PagingData
import com.powerly.core.data.model.SourceStatus
import com.powerly.core.data.model.SourcesStatus
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.Connector
import com.powerly.core.model.powerly.Media
import com.powerly.core.model.powerly.Review
import kotlinx.coroutines.flow.Flow


interface PowerSourceRepository {

    /**
     * Retrieves near power sources.
     *
     * @param latitude The latitude.
     * @param longitude The longitude.
     * @return  [com.powerly.core.data.model.SourcesStatus] results.
     */
    suspend fun getNearPowerSources(
        latitude: Double,
        longitude: Double
    ): SourcesStatus


    /**
     * Retrieves media list for a power source.
     * @param id The ID of the power source.
     * @return  a list of media.
     */
    suspend fun getMedia(id: String): List<Media>

    /**
     * Retrieves reviews for a power source.
     * @param id The ID of the power source.
     * @return  a paginated list of reviews.
     */
    fun getReviews(id: String): Flow<PagingData<Review>>

    /**
     * Retrieves a power source by ID.
     *
     * @param id The ID of the power source.
     * @return  [SourceStatus] results containing the power source.
     */
    suspend fun getPowerSource(id: String): SourceStatus

    /**
     * Retrieves connectors.
     *
     * @return  [ApiStatus] results.
     */
    suspend fun connectors(): ApiStatus<List<Connector>>

    /**
     * Retrieves power source details by identifier.
     * @param identifier The power source identifier.
     * @return  [SourceStatus] results.
     */
    suspend fun getPowerSourceByIdentifier(identifier: String): SourceStatus

}




