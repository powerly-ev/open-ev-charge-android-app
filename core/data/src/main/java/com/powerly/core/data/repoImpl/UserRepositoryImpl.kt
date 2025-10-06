package com.powerly.core.data.repoImpl

import com.powerly.core.data.model.AuthStatus
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.data.storage.StorageManager
import com.powerly.core.model.api.ApiErrorConstants
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.user.LogoutBody
import com.powerly.core.model.user.User
import com.powerly.core.model.user.UserUpdateBody
import com.powerly.core.network.RemoteDataSource
import com.powerly.core.network.asErrorMessage
import com.powerly.core.network.asValidationErrorMessage
import com.powerly.core.network.needToRefreshToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class UserRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val storageManager: StorageManager,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : UserRepository {
    private val _userFlow = MutableStateFlow<User?>(storageManager.userDetails)
    override val userFlow: Flow<User?> = _userFlow.asStateFlow()

    override val isLoggedIn: Boolean get() = storageManager.isLoggedIn

    override suspend fun updateUserDetails(
        request: UserUpdateBody
    ) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.updateUser(request)
            if (response.hasData) {
                val user = response.getData
                _userFlow.value = user
                if (request.currency != null) {
                    storageManager.pinUserCurrency = true
                }
                ApiStatus.Success(user)
            } else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            val msg = if (e.code() == ApiErrorConstants.VALIDATION) {
                e.asValidationErrorMessage
            } else {
                e.asErrorMessage
            }
            ApiStatus.Error(msg)
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }


    override suspend fun refreshToken() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.refreshToken()
            if (response.hasData) {
                val token = response.getData.accessToken.orEmpty()
                ApiStatus.Success(token)
            } else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }


    override suspend fun getUserDetails() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.getUser()
            if (response.hasData) {
                val user = response.getData
                ApiStatus.Success(user)
            } else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun checkAuthentication() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.authCheck()
            if (response.isSuccessful) {
                val body = response.body()!!
                val headers = response.headers()
                if (headers.needToRefreshToken) AuthStatus.RefreshToken
                else if (body.hasData) AuthStatus.Success(body.getData)
                else AuthStatus.Error(body.getMessage())
            } else AuthStatus.Error(response.asErrorMessage)
        } catch (e: Exception) {
            AuthStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun logout() = withContext(ioDispatcher) {
        try {
            val imei = storageManager.imei()
            val request = LogoutBody(imei)
            val response = remoteDataSource.authLogout(request)
            if (response.isSuccessful) {
                storageManager.logOutAll()
                storageManager.showRegisterNotification = true
                ApiStatus.Success(true)
            } else ApiStatus.Error(response.asErrorMessage)
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }


    override suspend fun deleteUser() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.deleteUser()
            if (response.isSuccessful) {
                storageManager.logOutAll()
                storageManager.showRegisterNotification = true
                ApiStatus.Success(true)
            } else ApiStatus.Error(response.asErrorMessage)
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

}
