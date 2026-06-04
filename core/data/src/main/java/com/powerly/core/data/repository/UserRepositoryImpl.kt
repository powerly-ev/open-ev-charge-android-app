package com.powerly.core.data.repository

import com.powerly.core.data.datasource.remote.UserRemoteDataSource
import com.powerly.core.database.StorageManager
import com.powerly.core.domain.repository.UserRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.user.User
import com.powerly.core.model.user.UserUpdateBody
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
internal class UserRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
    private val storageManager: StorageManager,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : UserRepository {
    override val userFlow: Flow<User?> = storageManager.userFlow
    override val currencyFlow: Flow<String> = storageManager.currencyFlow
    override val languageFlow = storageManager.languageFlow
    override val loggedInFlow = storageManager.loggedInFlow

    override suspend fun isLoggedIn(): Boolean = storageManager.isLoggedIn()

    override suspend fun getCurrency(): String = storageManager.getCurrency()

    override suspend fun getLanguage(): String = storageManager.getCurrentLanguage()

    override suspend fun updateLocallBalance(balance: Double) {
        val user = storageManager.getUser() ?: return
        user.balance = balance
        storageManager.saveUser(user)
    }

    override suspend fun updateUserDetails(
        request: UserUpdateBody
    ) = withContext(ioDispatcher) {
        val result = remoteDataSource.updateUser(request)
        if (result is ApiStatus.Success) {
            storageManager.saveUser(result.data)
            if (request.currency != null) storageManager.setPinUserCurrency(true)
        }
        result
    }

    override suspend fun refreshToken() = withContext(ioDispatcher) {
        remoteDataSource.refreshToken()
    }

    override suspend fun getUserDetails() = withContext(ioDispatcher) {
        val result = remoteDataSource.getUser()
        if (result is ApiStatus.Success) storageManager.saveUser(result.data)
        result
    }

    override suspend fun checkAuthentication() = withContext(ioDispatcher) {
        remoteDataSource.checkAuthentication()
    }

    private val _logoutEvent = MutableSharedFlow<Boolean>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val logoutEvent: SharedFlow<Boolean> = _logoutEvent.asSharedFlow()

    override suspend fun logout() = withContext(ioDispatcher) {
        val imei = storageManager.getUniqueId()
        val result = remoteDataSource.logout(imei)
        if (result is ApiStatus.Success) {
            _logoutEvent.emit(true)
            storageManager.clearLoginData()
            storageManager.setShowRegisterNotification(true)
        }
        result
    }

    override suspend fun deleteUser() = withContext(ioDispatcher) {
        val result = remoteDataSource.deleteUser()
        if (result is ApiStatus.Success) {
            storageManager.clearLoginData()
            storageManager.setShowRegisterNotification(true)
        }
        result
    }

    override suspend fun clearLoginData() {
        storageManager.clearLoginData()
    }
}
