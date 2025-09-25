package com.powerly.core.data.repoImpl

import com.powerly.core.data.model.BalanceRefillStatus
import com.powerly.core.data.repositories.PaymentRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.payment.AddCardBody
import com.powerly.core.model.payment.BalanceRefillBody
import com.powerly.core.network.RemoteDataSource
import com.powerly.core.network.asErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single


@Single
class PaymentRepositoryImpl (
    private val remoteDataSource: RemoteDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : PaymentRepository {

    override suspend fun cardList() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.cardList()
            if (response.hasData) ApiStatus.Success(response.getData)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun addCard(token: String) = withContext(ioDispatcher) {
        try {
            val request = AddCardBody(token)
            val response = remoteDataSource.cardAdd(request)
            if (response.isSuccess) ApiStatus.Success(true)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun setDefaultCard(id: String) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.cardDefault(id)
            if (response.isSuccess) ApiStatus.Success(true)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun deleteCard(id: String) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.cardDelete(id)
            if (response.isSuccess) ApiStatus.Success(true)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun refillBalance(offerId: Int, paymentMethodId: String) =
        withContext(ioDispatcher) {
            try {
                val body = BalanceRefillBody(offerId, paymentMethodId)
                val response = remoteDataSource.refillBalance(body)
                if (response.hasData) {
                    val result = response.getData
                    val balance = result.newBalance
                    val message = response.getMessage()
                    if (result.hasRedirectUrl)
                        BalanceRefillStatus.Authenticate(result.redirectUrl, message)
                    else
                        BalanceRefillStatus.Success(balance, message)
                } else BalanceRefillStatus.Error(response.getMessage())
            } catch (e: HttpException) {
                BalanceRefillStatus.Error(e.asErrorMessage)
            } catch (e: Exception) {
                BalanceRefillStatus.Error(e.asErrorMessage)
            }
        }


    override suspend fun getBalanceItems(countryId: Int?) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.getBalanceItems(countryId)
            if (response.isSuccess) ApiStatus.Success(response.getData)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun walletList() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.walletList()
            if (response.hasData) ApiStatus.Success(response.getData)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun walletPayout() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.walletPayout()
            if (response.isSuccess) ApiStatus.Success(response.getMessage())
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }
}