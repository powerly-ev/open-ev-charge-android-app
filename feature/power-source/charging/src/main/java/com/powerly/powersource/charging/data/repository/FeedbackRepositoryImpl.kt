package com.powerly.powersource.charging.data.repository

import com.powerly.core.domain.model.ApiStatus
import com.powerly.powersource.charging.data.datasource.remote.ChargingRemoteDataSource
import com.powerly.powersource.charging.domain.model.ReviewOptionsStatus
import com.powerly.powersource.charging.domain.repository.FeedbackRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
internal class FeedbackRepositoryImpl(
    private val remoteDataSource: ChargingRemoteDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : FeedbackRepository {

    override val reviewOptions = flow {
        when (val result = remoteDataSource.reviewOptions()) {
            is ApiStatus.Success -> emit(ReviewOptionsStatus.Success(result.data))
            is ApiStatus.Error -> emit(ReviewOptionsStatus.Error(result.msg))
            else -> emit(ReviewOptionsStatus.Loading)
        }
    }

    override suspend fun reviewAdd(
        orderId: String,
        rating: Double,
        msg: String
    ) = withContext(ioDispatcher) {
        remoteDataSource.reviewAdd(orderId, rating, msg)
    }

    override suspend fun reviewSkip(orderId: String) = withContext(ioDispatcher) {
        remoteDataSource.reviewSkip(orderId)
    }
}
