package com.powerly.core.data.repoImpl

import com.powerly.core.data.repositories.LoginEmailRepository
import com.powerly.core.database.StorageManager
import com.powerly.core.model.api.ApiErrorConstants
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.user.EmailCheckBody
import com.powerly.core.model.user.EmailForgetBody
import com.powerly.core.model.user.EmailLoginBody
import com.powerly.core.model.user.EmailRegisterBody
import com.powerly.core.model.user.EmailResetBody
import com.powerly.core.model.user.EmailVerifyResendBody
import com.powerly.core.model.user.User
import com.powerly.core.model.user.VerificationBody
import com.powerly.core.network.RemoteDataSource
import com.powerly.core.network.asErrorMessage
import com.powerly.core.network.asValidationErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import retrofit2.HttpException

@Single
class LoginEmailRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val storageManager: StorageManager,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : LoginEmailRepository {

    override val userFlow: Flow<User?> = storageManager.userFlow

    override val isLoggedIn: Boolean get() = storageManager.isLoggedIn

    override suspend fun emailCheck(email: String) = withContext(ioDispatcher) {
        try {
            val request = EmailCheckBody(email)
            val response = remoteDataSource.emailCheck(request)
            if (response.hasData) ApiStatus.Success(response.getData!!)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun emailLogin(
        email: String,
        password: String
    ) = withContext(ioDispatcher) {
        try {
            val imei = storageManager.imei()
            val body = EmailLoginBody(email, password, imei)
            val response = remoteDataSource.emailLogin(body)
            if (response.hasData) {
                val user = response.getData!!
                storageManager.saveLogin(user)
                ApiStatus.Success(user)
            } else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            val msg = if (e.code() == ApiErrorConstants.VALIDATION) e.asValidationErrorMessage
            else e.asErrorMessage
            ApiStatus.Error(msg)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun emailRegister(
        email: String,
        password: String,
        countryId: Int
    ) = withContext(ioDispatcher) {
        try {
            val body = EmailRegisterBody(
                email = email,
                password = password,
                countryId = countryId,
                deviceImei = storageManager.imei()
            )
            val response = remoteDataSource.emailRegister(body)
            if (response.hasData) ApiStatus.Success(response.getData!!)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            val msg = if (e.code() == ApiErrorConstants.VALIDATION) e.asValidationErrorMessage
            else e.asErrorMessage
            ApiStatus.Error(msg)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun emailVerify(
        code: String,
        email: String
    ) = withContext(ioDispatcher) {
        try {
            val body = VerificationBody(code, email)
            val response = remoteDataSource.emailVerify(body)
            if (response.hasData) {
                val user = response.getData!!
                storageManager.saveLogin(user)
                ApiStatus.Success(user)
            } else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            val msg = if (e.code() == ApiErrorConstants.VALIDATION) e.asValidationErrorMessage
            else e.asErrorMessage
            ApiStatus.Error(msg)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun emailVerifyResend(verificationToken: String) = withContext(ioDispatcher) {
        try {
            val request = EmailVerifyResendBody(verificationToken)
            val response = remoteDataSource.emailVerifyResend(request)
            if (response.hasData) ApiStatus.Success(response.getData!!)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun emailForgetPassword(email: String) = withContext(ioDispatcher) {
        try {
            val request = EmailForgetBody(email)
            val response = remoteDataSource.emailPasswordForget(request)
            if (response.hasData) ApiStatus.Success(response.getData!!)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun emailResetPassword(request: EmailResetBody) =
        withContext(ioDispatcher) {
            try {
                val response = remoteDataSource.emailPasswordReset(request)
                if (response.isSuccess) ApiStatus.Success(true)
                else ApiStatus.Error(response.getMessage())
            } catch (e: HttpException) {
                ApiStatus.Error(e.asErrorMessage)
            } catch (e: Exception) {
                ApiStatus.Error(e.asErrorMessage)
            }
        }

    override suspend fun emailResetResend(email: String) = withContext(ioDispatcher) {
        try {
            val request = EmailVerifyResendBody(email)
            val response = remoteDataSource.emailPasswordResetResend(request)
            if (response.hasData) ApiStatus.Success(response.getData!!)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            ApiStatus.Error(e.asErrorMessage)
        }
    }
}