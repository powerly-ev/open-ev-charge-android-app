package com.powerly.core.data.repoImpl

import com.powerly.core.data.repositories.LoginEmailRepository
import com.powerly.core.model.api.ApiErrorConstants
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.user.EmailCheckBody
import com.powerly.core.model.user.EmailForgetBody
import com.powerly.core.model.user.EmailLoginBody
import com.powerly.core.model.user.EmailRegisterBody
import com.powerly.core.model.user.EmailResetBody
import com.powerly.core.model.user.EmailVerifyResendBody
import com.powerly.core.model.user.VerificationBody
import com.powerly.core.network.RemoteDataSource
import com.powerly.core.network.asErrorMessage
import com.powerly.core.network.asValidationErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import retrofit2.HttpException

@Single
class LoginEmailRepositoryImpl (
    private val remoteDataSource: RemoteDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : LoginEmailRepository {

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

    override suspend fun emailLogin(request: EmailLoginBody) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.emailLogin(request)
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

    override suspend fun emailRegister(request: EmailRegisterBody) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.emailRegister(request)
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

    override suspend fun emailVerify(request: VerificationBody) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.emailVerify(request)
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