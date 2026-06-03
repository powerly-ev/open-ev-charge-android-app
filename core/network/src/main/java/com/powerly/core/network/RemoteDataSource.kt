package com.powerly.core.network

import com.powerly.core.model.api.ApiResponse
import com.powerly.core.model.api.BaseResponsePaginated
import com.powerly.core.model.location.AppCurrency
import com.powerly.core.model.location.Country
import com.powerly.core.model.powerly.Connector
import com.powerly.core.model.powerly.Media
import com.powerly.core.model.powerly.PowerSource
import com.powerly.core.model.powerly.Review
import com.powerly.core.model.powerly.Session
import com.powerly.core.model.powerly.StopChargingBody
import com.powerly.core.model.user.DeviceBody
import com.powerly.core.model.user.LogoutBody
import com.powerly.core.model.user.RefreshToken
import com.powerly.core.model.user.SocialLoginBody
import com.powerly.core.model.user.User
import com.powerly.core.model.user.UserUpdateBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RemoteDataSource(private val client: HttpClient) {

    suspend fun getCountries(): ApiResponse<List<Country>> {
        return client.get(ApiEndPoints.COUNTRIES).body()
    }

    suspend fun getCurrencies(): ApiResponse<List<AppCurrency>> {
        return client.get(ApiEndPoints.COUNTRIES_CURRENCIES).body()
    }

    suspend fun getCountry(id: Int): ApiResponse<Country> {
        val endpoint = ApiEndPoints.COUNTRY.replace("{id}", id.toString())
        return client.get(endpoint).body()
    }

    suspend fun updateDevice(body: DeviceBody): HttpResponse {
        return client.put(ApiEndPoints.DEVICE) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    // Authentication
    suspend fun authLogout(request: LogoutBody): HttpResponse {
        return client.post(ApiEndPoints.AUTH_LOGOUT) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun refreshToken(): ApiResponse<RefreshToken> {
        return client.get(ApiEndPoints.AUTH_TOKEN_REFRESH).body()
    }

    // Device account social
    suspend fun socialGoogleLogin(request: SocialLoginBody): ApiResponse<User?> {
        return client.post(ApiEndPoints.AUTH_GOOGLE) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun socialHuaweiLogin(request: SocialLoginBody): ApiResponse<User?> {
        return client.post(ApiEndPoints.AUTH_HUAWEI) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // User
    suspend fun deleteUser(): HttpResponse {
        return client.delete(ApiEndPoints.USER_ME)
    }

    suspend fun updateUser(request: UserUpdateBody): ApiResponse<User> {
        return client.put(ApiEndPoints.USER_ME) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getUser(): ApiResponse<User> {
        return client.get(ApiEndPoints.USER_ME).body()
    }

    suspend fun authCheck(): HttpResponse {
        return client.get(ApiEndPoints.USER_ME)
    }

    // Power sources
    suspend fun getPowerSource(id: String): ApiResponse<PowerSource?> {
        val endpoint = ApiEndPoints.POWER_SOURCE_ACTION.replace("{id}", id)
        return client.get(endpoint).body()
    }

    suspend fun getNearPowerSources(
        latitude: Double,
        longitude: Double,
        search: String?
    ): BaseResponsePaginated<PowerSource> {
        return client.get(ApiEndPoints.POWER_SOURCE) {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            if (search != null) parameter("search", search)
        }.body()
    }

    suspend fun getMedia(id: String): ApiResponse<List<Media>?> {
        val endpoint = ApiEndPoints.POWER_SOURCE_MEDIA.replace("{id}", id)
        return client.get(endpoint).body()
    }

    suspend fun getReviews(
        id: String, page: Int, limit: Int = 15
    ): BaseResponsePaginated<Review> {
        val endpoint = ApiEndPoints.POWER_SOURCE_REVIEWS.replace("{id}", id)
        return client.get(endpoint) {
            parameter("page", page)
            parameter("limit", limit)
        }.body()
    }

    suspend fun stopCharging(body: StopChargingBody): ApiResponse<Session?> {
        return client.post(ApiEndPoints.POWER_SOURCE_CHARGING_STOP) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }

    suspend fun powerSourceDetails(identifier: String): ApiResponse<PowerSource?> {
        val endpoint = ApiEndPoints.POWER_SOURCE_TOKEN.replace("{identifier}", identifier)
        return client.get(endpoint).body()
    }

    // Sessions
    suspend fun getActiveOrders(
        page: Int,
        status: String = "active",
        limit: Int = 15
    ): BaseResponsePaginated<Session> {
        return client.get(ApiEndPoints.POWER_SOURCE_ORDERS) {
            parameter("page", page)
            parameter("status", status)
            parameter("limit", limit)
        }.body()
    }

    suspend fun getCompleteOrders(
        page: Int,
        status: String = "complete",
        limit: Int = 15
    ): BaseResponsePaginated<Session> {
        return client.get(ApiEndPoints.POWER_SOURCE_ORDERS) {
            parameter("page", page)
            parameter("status", status)
            parameter("limit", limit)
        }.body()
    }

    suspend fun vehiclesConnectors(): ApiResponse<List<Connector>?> {
        return client.get(ApiEndPoints.VEHICLE_CONNECTORS).body()
    }

    companion object {
        private const val TAG = "NewRemoteDataSource"
    }
}