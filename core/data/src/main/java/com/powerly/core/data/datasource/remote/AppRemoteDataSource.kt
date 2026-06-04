package com.powerly.core.data.datasource.remote

import com.powerly.core.data.api.AppApi
import com.powerly.core.domain.model.CurrenciesStatus
import com.powerly.core.model.api.ApiResponse
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.location.AppCurrency
import com.powerly.core.model.location.Country
import com.powerly.core.model.user.DeviceBody
import com.powerly.core.network.KtorClient
import com.powerly.core.network.safeApiActionRaw
import com.powerly.core.network.safeApiCall
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single
internal class AppRemoteDataSource(
    ktorClient: KtorClient,
) {
    private val client = ktorClient.client

    suspend fun getCountries(): ApiStatus<List<Country>> = safeApiCall {
        client.get(AppApi.COUNTRIES).body<ApiResponse<List<Country>?>>()
    }

    suspend fun getCountry(id: Int): ApiStatus<Country> = safeApiCall {
        val endpoint = AppApi.COUNTRY.replace("{id}", id.toString())
        client.get(endpoint).body<ApiResponse<Country?>>()
    }

    suspend fun getCurrencies(): CurrenciesStatus {
        val result = safeApiCall<List<AppCurrency>> {
            client.get(AppApi.CURRENCIES).body<ApiResponse<List<AppCurrency>?>>()
        }
        return when (result) {
            is ApiStatus.Success -> CurrenciesStatus.Success(result.data)
            is ApiStatus.Error -> CurrenciesStatus.Error(result.msg)
            is ApiStatus.Loading -> CurrenciesStatus.Loading
        }
    }

    suspend fun updateDevice(body: DeviceBody): ApiStatus<Boolean> = safeApiActionRaw {
        client.put(AppApi.DEVICE) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }
}
