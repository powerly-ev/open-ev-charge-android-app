package com.powerly.powersource.charging.data.datasource.remote

import com.powerly.core.model.api.ApiResponse
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.ReviewBody
import com.powerly.core.model.powerly.Session
import com.powerly.core.model.powerly.StartChargingBody
import com.powerly.core.model.powerly.StopChargingBody
import com.powerly.core.network.KtorClient
import com.powerly.core.network.safeApiCall
import com.powerly.powersource.charging.data.api.ChargingApi
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
    ): ApiStatus<Session> = safeApiCall {
        client.post(ChargingApi.ORDERS) {
            contentType(ContentType.Application.Json)
            setBody(
                StartChargingBody(
                    powersourceId = chargePointId,
                    quantity = quantity,
                    connector = connector
                )
            )
        }.body<ApiResponse<Session?>>()
    }

    suspend fun stopCharging(
        orderId: String,
        chargePointId: String,
        connector: Int?
    ): ApiStatus<Session> = safeApiCall {
        client.post(ChargingApi.CHARGING_STOP) {
            contentType(ContentType.Application.Json)
            setBody(
                StopChargingBody(
                    orderId = orderId,
                    powersourceId = chargePointId,
                    connector = connector
                )
            )
        }.body<ApiResponse<Session?>>()
    }

    suspend fun sessionDetails(orderId: String): ApiStatus<Session> = safeApiCall {
        client.get(ChargingApi.ORDER_DETAILS.replace("{orderId}", orderId))
            .body<ApiResponse<Session?>>()
    }

    // Feedback

    suspend fun reviewOptions(): ApiStatus<Map<String, List<String>>> = safeApiCall {
        client.get(ChargingApi.REVIEW_OPTIONS)
            .body<ApiResponse<Map<String, List<String>>?>>()
    }

    suspend fun reviewAdd(
        orderId: String,
        rating: Double,
        msg: String
    ): ApiStatus<Session> = safeApiCall {
        client.post(ChargingApi.REVIEW_ADD.replace("{order_id}", orderId)) {
            contentType(ContentType.Application.Json)
            setBody(ReviewBody(rating = rating, msg = msg))
        }.body<ApiResponse<Session?>>()
    }

    suspend fun reviewSkip(orderId: String): ApiStatus<Session> = safeApiCall {
        client.post(ChargingApi.REVIEW_SKIP.replace("{order_id}", orderId))
            .body<ApiResponse<Session?>>()
    }
}
