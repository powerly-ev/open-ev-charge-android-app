package com.powerly.core.data.repoImpl

import android.util.Log
import com.powerly.core.data.model.AuthStatus
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.database.StorageManager
import com.powerly.core.model.api.ApiResponse
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.user.LogoutBody
import com.powerly.core.model.user.User
import com.powerly.core.model.user.UserUpdateBody
import com.powerly.core.network.RemoteDataSource
import com.powerly.core.network.asErrorMessage
import com.powerly.core.network.isSuccessful
import com.powerly.core.network.needToRefreshToken
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class UserRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val storageManager: StorageManager,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : UserRepository {
    override val userFlow: Flow<User?> = storageManager.userFlow

    override val isLoggedIn: Boolean get() = storageManager.isLoggedIn

    override suspend fun updateUserDetails(
        request: UserUpdateBody
    ) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.updateUser(request)
            if (response.hasData) {
                val user = response.getData()
                storageManager.userDetails = user
                if (request.currency != null) {
                    storageManager.pinUserCurrency = true
                }
                ApiStatus.Success(user)
            } else ApiStatus.Error(response.getMessage())
        } catch (e: ResponseException) {
            ApiStatus.Error(e.asErrorMessage())
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage())
        }
    }


    override suspend fun refreshToken() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.refreshToken()
            if (response.hasData) {
                val token = response.getData().accessToken.orEmpty()
                ApiStatus.Success(token)
            } else ApiStatus.Error(response.getMessage())
        } catch (e: ResponseException) {
            ApiStatus.Error(e.asErrorMessage())
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage())
        }
    }


    override suspend fun getUserDetails() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.getUser()
            Log.v("UserRepository", response.toString())
            if (response.hasData) {
                val user = response.getData()
                storageManager.userDetails = user
                ApiStatus.Success(user)
            } else ApiStatus.Error(response.getMessage())
        } catch (e: ResponseException) {
            ApiStatus.Error(e.asErrorMessage())
        } catch (e: Exception) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage())
        }
    }

    override suspend fun checkAuthentication() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.authCheck()
            if (response.isSuccessful) {
                val body = response.body<ApiResponse<User>>()
                if (response.headers.needToRefreshToken()) AuthStatus.RefreshToken
                else if (body.hasData) AuthStatus.Success(body.getData())
                else AuthStatus.Error(body.getMessage())
            } else AuthStatus.Error(response.asErrorMessage())
        } catch (e: Exception) {
            AuthStatus.Error(e.asErrorMessage())
        }
    }

    private val _logoutEvent = MutableSharedFlow<Boolean>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val logoutEvent: SharedFlow<Boolean> = _logoutEvent.asSharedFlow()

    override suspend fun logout() = withContext(ioDispatcher) {
        try {
            val imei = storageManager.imei()
            val request = LogoutBody(imei)
            val response = remoteDataSource.authLogout(request)
            if (response.isSuccessful) {
                _logoutEvent.emit(true)
                storageManager.clearLoginData()
                storageManager.showRegisterNotification = true
                ApiStatus.Success(true)
            } else ApiStatus.Error(response.asErrorMessage())
        } catch (e: ResponseException) {
            ApiStatus.Error(e.asErrorMessage())
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage())
        }
    }


    override suspend fun deleteUser() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.deleteUser()
            if (response.isSuccessful) {
                storageManager.clearLoginData()
                storageManager.showRegisterNotification = true
                ApiStatus.Success(true)
            } else ApiStatus.Error(response.asErrorMessage())
        } catch (e: ResponseException) {
            ApiStatus.Error(e.asErrorMessage())
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage())
        }
    }

    override suspend fun clearLoginData() {
        storageManager.clearLoginData()
    }
}
