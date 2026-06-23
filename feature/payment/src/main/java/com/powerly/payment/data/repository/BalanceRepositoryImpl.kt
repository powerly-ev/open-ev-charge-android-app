package com.powerly.payment.data.repository

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.network.asErrorMessage
import com.powerly.payment.data.datasource.remote.PaymentRemoteDataSource
import com.powerly.payment.domain.model.BalanceRefillStatus
import com.powerly.payment.domain.repository.BalanceRepository
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
internal class BalanceRepositoryImpl(
    private val remoteDataSource: PaymentRemoteDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher,
) : BalanceRepository {

    override suspend fun refillBalance(
        offerId: Int,
        paymentMethodId: String
    ): BalanceRefillStatus = withContext(ioDispatcher) {
        when (val response = remoteDataSource.refillBalance(offerId, paymentMethodId)) {
            is ApiStatus.Error -> {
                BalanceRefillStatus.Error(response.msg)
            }

            is ApiStatus.Success -> {
                val result = response.data
                val balance = result.newBalance
                val message = response.msg!!
                val redirectUrl = result.redirectUrl
                if (redirectUrl != null) {
                    BalanceRefillStatus.Authenticate(redirectUrl, message, balance)
                } else {
                    BalanceRefillStatus.Success(balance, message)
                }
            }

            else -> BalanceRefillStatus.Loading
        }
    }

    override suspend fun getBalanceItems(countryId: Int?) = withContext(ioDispatcher) {
        remoteDataSource.getBalanceItems(countryId)
    }

    override suspend fun walletList() = withContext(ioDispatcher) {
        remoteDataSource.walletList()
    }

    override suspend fun walletPayout() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.walletPayout()
            if (response.isSuccess) ApiStatus.Success(response.getMessage())
            else ApiStatus.Error(response.getMessage())
        } catch (e: ResponseException) {
            ApiStatus.Error(e.asErrorMessage())
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage())
        }
    }
}
