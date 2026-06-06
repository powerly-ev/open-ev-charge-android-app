package com.powerly.payment.data.datasource.remote

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.map
import com.powerly.payment.domain.model.BalanceItem
import com.powerly.payment.domain.model.StripCard
import com.powerly.payment.domain.model.Wallet
import com.powerly.core.network.KtorClient
import com.powerly.core.network.api.ApiResponse
import com.powerly.core.network.safeApiAction
import com.powerly.core.network.safeApiCall
import com.powerly.payment.data.api.PaymentApi
import com.powerly.payment.data.model.AddCardBody
import com.powerly.payment.data.model.BalanceRefillBody
import com.powerly.payment.data.model.BalanceRefillResponse
import com.powerly.payment.data.model.dto.BalanceItemDto
import com.powerly.payment.data.model.dto.StripCardDto
import com.powerly.payment.data.model.dto.WalletDto
import com.powerly.payment.data.model.dto.toDomain
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single
internal class PaymentRemoteDataSource(
    ktorClient: KtorClient,
) {
    private val client = ktorClient.client

    // Cards

    suspend fun cardList(): ApiStatus<List<StripCard>> = safeApiCall<List<StripCardDto>> {
        client.get(PaymentApi.PAYMENT_CARDS).body()
    }.map { dtos -> dtos.map { it.toDomain() } }

    suspend fun cardAdd(token: String): ApiStatus<Boolean> = safeApiAction {
        client.post(PaymentApi.PAYMENT_CARDS) {
            contentType(ContentType.Application.Json)
            setBody(AddCardBody(token))
        }.body<ApiResponse<List<StripCardDto>?>>()
    }

    suspend fun cardDefault(id: String): ApiStatus<Boolean> = safeApiAction {
        client.post(PaymentApi.cardDefault(id)).body<ApiResponse<StripCardDto?>>()
    }

    suspend fun cardDelete(id: String): ApiStatus<Boolean> = safeApiAction {
        client.delete(PaymentApi.cardDelete(id)).body<ApiResponse<StripCardDto?>>()
    }

    // Balance
    suspend fun refillBalance(
        offerId: Int,
        paymentMethodId: String
    ): ApiStatus<BalanceRefillResponse> = safeApiCall {
        client.post(PaymentApi.BALANCE_REFILL) {
            contentType(ContentType.Application.Json)
            setBody(BalanceRefillBody(offerId, paymentMethodId))
        }.body()
    }

    suspend fun getBalanceItems(countryId: Int?): ApiStatus<List<BalanceItem>> =
        safeApiCall<List<BalanceItemDto>> {
            client.get(PaymentApi.BALANCE_OFFERS) {
                if (countryId != null) parameter("country_id", countryId)
            }.body()
        }.map { dtos -> dtos.map { it.toDomain() } }

    suspend fun walletList(): ApiStatus<List<Wallet>> = safeApiCall<List<WalletDto>> {
        client.get(PaymentApi.PAYOUTS).body()
    }.map { dtos -> dtos.map { it.toDomain() } }

    suspend fun walletPayout(): ApiResponse<*> {
        return client.post(PaymentApi.PAYOUTS_REQUEST).body()
    }
}
