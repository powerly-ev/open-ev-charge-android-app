package com.powerly.core.data.datasource.remote

import com.powerly.core.data.api.UserApi
import com.powerly.core.domain.model.AuthStatus
import com.powerly.core.network.api.ApiResponse
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.map
import com.powerly.core.data.model.LogoutBody
import com.powerly.core.data.model.RefreshToken
import com.powerly.core.data.model.UserUpdateBody
import com.powerly.core.model.user.User
import com.powerly.core.network.KtorClient
import com.powerly.core.network.asErrorMessage
import com.powerly.core.network.isSuccessful
import com.powerly.core.network.needToRefreshToken
import com.powerly.core.network.safeApiActionRaw
import com.powerly.core.network.safeApiCall
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single
internal class UserRemoteDataSource(
    ktorClient: KtorClient,
) {
    private val client = ktorClient.client

    suspend fun getUser(): ApiStatus<User> = safeApiCall {
        client.get(UserApi.ME).body<ApiResponse<User?>>()
    }

    suspend fun updateUser(
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        password: String? = null,
        vatId: String? = null,
        countryId: Int? = null,
        currency: String? = null,
        latitude: Double? = null,
        longitude: Double? = null
    ): ApiStatus<User> = safeApiCall {
        val body = UserUpdateBody(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            vatId = vatId,
            countryId = countryId,
            currency = currency,
            latitude = latitude,
            longitude = longitude
        )
        client.put(UserApi.ME) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body<ApiResponse<User?>>()
    }

    suspend fun refreshToken(): ApiStatus<String> = safeApiCall<RefreshToken> {
        client.get(UserApi.TOKEN_REFRESH).body<ApiResponse<RefreshToken?>>()
    }.map { it.accessToken.orEmpty() }

    /**
     * Auth check is special: it inspects response headers (`Token-Refresh-Required`)
     * to decide between [AuthStatus.Success] and [AuthStatus.RefreshToken], so it
     * can't go through the generic [safeApiCall] helper.
     */
    suspend fun checkAuthentication(): AuthStatus = try {
        val response = client.get(UserApi.ME)
        if (response.isSuccessful) {
            val body = response.body<ApiResponse<User>>()
            when {
                response.headers.needToRefreshToken() -> AuthStatus.RefreshToken
                body.hasData -> AuthStatus.Success(body.getData())
                else -> AuthStatus.Error(body.getMessage())
            }
        } else AuthStatus.Error(response.asErrorMessage())
    } catch (e: Exception) {
        e.printStackTrace()
        AuthStatus.Error(e.asErrorMessage())
    }

    suspend fun logout(imei: String): ApiStatus<Boolean> = safeApiActionRaw {
        client.post(UserApi.LOGOUT) {
            contentType(ContentType.Application.Json)
            setBody(LogoutBody(imei))
        }
    }

    suspend fun deleteUser(): ApiStatus<Boolean> = safeApiActionRaw {
        client.delete(UserApi.ME)
    }
}
