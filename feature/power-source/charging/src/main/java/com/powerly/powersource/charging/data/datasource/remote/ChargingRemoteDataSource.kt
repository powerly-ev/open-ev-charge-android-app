package com.powerly.powersource.charging.data.datasource.remote

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.map
import com.powerly.core.domain.model.powerly.Session
import com.powerly.core.network.KtorClient
import com.powerly.core.network.api.ApiResponse
import com.powerly.core.network.safeApiCall
import com.powerly.core.data.api.ApiEndpoints
import com.powerly.powersource.charging.data.model.ReviewBody
import com.powerly.core.data.model.powerly.SessionDto
import com.powerly.powersource.charging.data.model.StartChargingBody
import com.powerly.powersource.charging.data.model.StopChargingBody
import com.powerly.core.data.model.powerly.toDomain
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single
internal class ChargingRemoteDataSource(
    ktorClient: KtorClient,
) {
    private val client = ktorClient.client

    // Charging

    suspend fun startCharging(
        chargePointId: String,
        quantity: String,
        connector: Int?
    ): ApiStatus<Session> = safeApiCall<SessionDto> {
        client.post(ApiEndpoints.CHARGING_ORDERS) {
            contentType(ContentType.Application.Json)
            setBody(
                StartChargingBody(
                    powersourceId = chargePointId,
                    quantity = quantity,
                    connector = connector
                )
            )
        }.body<ApiResponse<SessionDto?>>()
    }.map { it.toDomain() }

    suspend fun stopCharging(
        orderId: String,
        chargePointId: String,
        connector: Int?
    ): ApiStatus<Session> = safeApiCall<SessionDto> {
        client.post(ApiEndpoints.CHARGING_STOP) {
            contentType(ContentType.Application.Json)
            setBody(
                StopChargingBody(
                    orderId = orderId,
                    powersourceId = chargePointId,
                    connector = connector
                )
            )
        }.body<ApiResponse<SessionDto?>>()
    }.map { it.toDomain() }

    suspend fun sessionDetails(orderId: String): ApiStatus<Session> = safeApiCall<SessionDto> {
        client.get(ApiEndpoints.orderDetails(orderId))
            .body<ApiResponse<SessionDto?>>()
    }.map { it.toDomain() }

    // Feedback

    suspend fun reviewOptions(): ApiStatus<Map<String, List<String>>> = safeApiCall {
        client.get(ApiEndpoints.REVIEW_OPTIONS)
            .body<ApiResponse<Map<String, List<String>>?>>()
    }

    suspend fun reviewAdd(
        orderId: String,
        rating: Double,
        msg: String
    ): ApiStatus<Session> = safeApiCall<SessionDto> {
        client.post(ApiEndpoints.reviewAdd(orderId)) {
            contentType(ContentType.Application.Json)
            setBody(ReviewBody(rating = rating, msg = msg))
        }.body<ApiResponse<SessionDto?>>()
    }.map { it.toDomain() }

    suspend fun reviewSkip(orderId: String): ApiStatus<Session> = safeApiCall<SessionDto> {
        client.post(ApiEndpoints.reviewSkip(orderId))
            .body<ApiResponse<SessionDto?>>()
    }.map { it.toDomain() }
}
