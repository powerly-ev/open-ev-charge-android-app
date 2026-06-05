package com.powerly.orders.data.datasource.remote

import com.powerly.core.network.api.ApiResponse
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.network.api.BaseResponsePaginated
import com.powerly.core.model.powerly.Session
import com.powerly.orders.data.model.StopChargingBody
import com.powerly.core.network.KtorClient
import com.powerly.core.network.safeApiCall
import com.powerly.orders.data.api.SessionsApi
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single
internal class SessionsRemoteDataSource(
    ktorClient: KtorClient,
) {
    private val client = ktorClient.client

    suspend fun stopCharging(
        orderId: String,
        chargePointId: String,
        connector: Int?
    ): ApiStatus<Session> = safeApiCall {
        client.post(SessionsApi.ORDERS_STOP) {
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

    suspend fun getActiveOrders(
        page: Int,
        limit: Int = 15
    ): BaseResponsePaginated<Session> {
        return client.get(SessionsApi.ORDERS) {
            parameter("page", page)
            parameter("status", "active")
            parameter("limit", limit)
        }.body()
    }

    suspend fun getCompleteOrders(
        page: Int,
        limit: Int = 15
    ): BaseResponsePaginated<Session> {
        return client.get(SessionsApi.ORDERS) {
            parameter("page", page)
            parameter("status", "complete")
            parameter("limit", limit)
        }.body()
    }
}
