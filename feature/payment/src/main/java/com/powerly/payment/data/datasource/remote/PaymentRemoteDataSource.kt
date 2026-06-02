package com.powerly.payment.data.datasource.remote

import com.powerly.core.model.api.ApiResponse
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.payment.BalanceItem
import com.powerly.core.model.payment.StripCard
import com.powerly.core.model.payment.Wallet
import com.powerly.core.network.KtorClient
import com.powerly.core.network.safeApiAction
import com.powerly.core.network.safeApiCall
import com.powerly.payment.data.api.PaymentApi
import com.powerly.payment.data.model.AddCardBody
import com.powerly.payment.data.model.BalanceRefillBody
import com.powerly.payment.data.model.BalanceRefillResponse
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

    suspend fun cardList(): ApiStatus<List<StripCard>> = safeApiCall {
        client.get(PaymentApi.PAYMENT_CARDS).body()
    }

    suspend fun cardAdd(token: String): ApiStatus<Boolean> = safeApiAction {
        client.post(PaymentApi.PAYMENT_CARDS) {
            contentType(ContentType.Application.Json)
            setBody(AddCardBody(token))
        }.body<ApiResponse<List<StripCard>?>>()
    }

    suspend fun cardDefault(id: String): ApiStatus<Boolean> = safeApiAction {
        client.post(PaymentApi.cardDefault(id)).body<ApiResponse<StripCard?>>()
    }

    suspend fun cardDelete(id: String): ApiStatus<Boolean> = safeApiAction {
        client.delete(PaymentApi.cardDelete(id)).body<ApiResponse<StripCard?>>()
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

    suspend fun getBalanceItems(countryId: Int?): ApiStatus<List<BalanceItem>> = safeApiCall {
        client.get(PaymentApi.BALANCE_OFFERS) {
            if (countryId != null) parameter("country_id", countryId)
        }.body()
    }

    suspend fun walletList(): ApiStatus<List<Wallet>> = safeApiCall {
        client.get(PaymentApi.PAYOUTS).body()
    }

    suspend fun walletPayout(): ApiResponse<*> {
        return client.post(PaymentApi.PAYOUTS_REQUEST).body()
    }
}
