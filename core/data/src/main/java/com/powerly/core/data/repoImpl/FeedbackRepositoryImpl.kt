package com.powerly.core.data.repoImpl

import com.powerly.core.data.model.ReviewOptionsStatus
import com.powerly.core.data.repositories.FeedbackRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.ReviewBody
import com.powerly.core.network.RemoteDataSource
import com.powerly.core.network.asErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import retrofit2.HttpException

@Single
class FeedbackRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : FeedbackRepository {

    override val reviewOptions = flow {
        try {
            val response = remoteDataSource.reviewOptions()
            emit(
                if (response.hasData) ReviewOptionsStatus.Success(response.getData!!)
                else ReviewOptionsStatus.Error(response.getMessage())
            )
        } catch (e: HttpException) {
            emit(ReviewOptionsStatus.Error(e.asErrorMessage))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ReviewOptionsStatus.Error(e.asErrorMessage))
        }
    }

    override suspend fun reviewAdd(
        orderId: String,
        rating: Double,
        msg: String
    ) = withContext(ioDispatcher) {
        try {
            val body = ReviewBody(rating = rating, msg = msg)
            val response = remoteDataSource.reviewAdd(
                orderId = orderId,
                body = body
            )
            if (response.hasData) ApiStatus.Success(response.getData!!)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun reviewSkip(orderId: String) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.reviewSkip(orderId)
            if (response.hasData) ApiStatus.Success(response.getData!!)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun reviewsList() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.reviewPending(limit = 1)
            if (response.hasData) ApiStatus.Success(response.getData?.getOrNull(0))
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }
}