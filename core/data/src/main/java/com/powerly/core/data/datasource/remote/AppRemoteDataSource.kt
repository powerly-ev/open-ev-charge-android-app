package com.powerly.core.data.datasource.remote

import com.powerly.core.data.api.AppApi
import com.powerly.core.data.model.DeviceBody
import com.powerly.core.data.model.location.AppCurrencyDto
import com.powerly.core.data.model.location.CountryDto
import com.powerly.core.data.model.location.toDomain
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.CurrenciesStatus
import com.powerly.core.domain.model.map
import com.powerly.core.domain.model.location.AppCurrency
import com.powerly.core.domain.model.location.Country
import com.powerly.core.network.KtorClient
import com.powerly.core.network.api.ApiResponse
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

    suspend fun getCountries(): ApiStatus<List<Country>> = safeApiCall<List<CountryDto>> {
        client.get(AppApi.COUNTRIES).body<ApiResponse<List<CountryDto>?>>()
    }.map { dtos -> dtos.map { it.toDomain() } }

    suspend fun getCountry(id: Int): ApiStatus<Country> = safeApiCall<CountryDto> {
        val endpoint = AppApi.COUNTRY.replace("{id}", id.toString())
        client.get(endpoint).body<ApiResponse<CountryDto?>>()
    }.map { it.toDomain() }

    suspend fun getCurrencies(): CurrenciesStatus {
        val result = safeApiCall<List<AppCurrencyDto>> {
            client.get(AppApi.CURRENCIES).body<ApiResponse<List<AppCurrencyDto>?>>()
        }.map { dtos -> dtos.map { it.toDomain() } }
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
