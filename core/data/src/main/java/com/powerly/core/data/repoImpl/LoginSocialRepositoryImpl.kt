package com.powerly.core.data.repoImpl

import com.powerly.core.data.repositories.LoginSocialRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.user.SocialLoginBody
import com.powerly.core.network.RemoteDataSource
import com.powerly.core.network.asErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import retrofit2.HttpException

@Single
class LoginSocialRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : LoginSocialRepository {

    override suspend fun googleLogin(request: SocialLoginBody) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.socialGoogleLogin(request)
            if (response.hasData) ApiStatus.Success(response.getData!!)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun huaweiLogin(request: SocialLoginBody) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.socialHuaweiLogin(request)
            if (response.hasData) ApiStatus.Success(response.getData!!)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }
}
