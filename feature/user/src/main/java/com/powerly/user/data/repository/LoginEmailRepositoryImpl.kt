package com.powerly.user.data.repository

import com.powerly.core.database.StorageManager
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.model.user.User
import com.powerly.user.data.datasource.remote.UserAuthRemoteDataSource
import com.powerly.user.domain.repository.LoginEmailRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
internal class LoginEmailRepositoryImpl(
    private val remoteDataSource: UserAuthRemoteDataSource,
    private val storageManager: StorageManager,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : LoginEmailRepository {

    override val userFlow: Flow<User?> = storageManager.userFlow

    override suspend fun emailCheck(email: String) = withContext(ioDispatcher) {
        remoteDataSource.emailCheck(email)
    }

    override suspend fun emailLogin(
        email: String,
        password: String
    ) = withContext(ioDispatcher) {
        val result = remoteDataSource.emailLogin(
            email = email,
            password = password,
            deviceImei = storageManager.getUniqueId()
        )
        if (result is ApiStatus.Success) storageManager.saveLogin(result.data)
        result
    }

    override suspend fun emailRegister(
        email: String,
        password: String,
        countryId: Int
    ) = withContext(ioDispatcher) {
        remoteDataSource.emailRegister(
            email = email,
            password = password,
            countryId = countryId,
            deviceImei = storageManager.getUniqueId()
        )
    }

    override suspend fun emailVerify(
        code: String,
        email: String
    ) = withContext(ioDispatcher) {
        val result = remoteDataSource.emailVerify(code, email)
        if (result is ApiStatus.Success) storageManager.saveLogin(result.data)
        result
    }

    override suspend fun emailVerifyResend(verificationToken: String) = withContext(ioDispatcher) {
        remoteDataSource.emailVerifyResend(verificationToken)
    }

    override suspend fun emailForgetPassword(email: String) = withContext(ioDispatcher) {
        remoteDataSource.emailPasswordForget(email)
    }

    override suspend fun emailResetPassword(
        pin: String,
        email: String,
        password: String
    ) = withContext(ioDispatcher) {
        remoteDataSource.emailPasswordReset(pin, email, password)
    }

    override suspend fun emailResetResend(email: String) = withContext(ioDispatcher) {
        remoteDataSource.emailPasswordResetResend(email)
    }
}
