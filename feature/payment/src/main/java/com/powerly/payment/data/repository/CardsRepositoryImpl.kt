package com.powerly.payment.data.repository

import com.powerly.payment.data.datasource.remote.PaymentRemoteDataSource
import com.powerly.payment.domain.repository.CardsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
internal class CardsRepositoryImpl(
    private val remoteDataSource: PaymentRemoteDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher,
) : CardsRepository {

    override suspend fun cardList() = withContext(ioDispatcher) {
        remoteDataSource.cardList()
    }

    override suspend fun addCard(token: String) = withContext(ioDispatcher) {
        remoteDataSource.cardAdd(token)
    }

    override suspend fun setDefaultCard(id: String) = withContext(ioDispatcher) {
        remoteDataSource.cardDefault(id)
    }

    override suspend fun deleteCard(id: String) = withContext(ioDispatcher) {
        remoteDataSource.cardDelete(id)
    }
}
