package com.powerly.user.data.datasource.remote

import com.powerly.core.network.api.ApiResponse
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.model.user.EmailCheck
import com.powerly.user.data.model.EmailCheckBody
import com.powerly.user.data.model.EmailForgetBody
import com.powerly.user.data.model.EmailLoginBody
import com.powerly.user.data.model.EmailRegisterBody
import com.powerly.user.data.model.EmailResetBody
import com.powerly.user.data.model.EmailVerifyResendBody
import com.powerly.core.model.user.User
import com.powerly.core.model.user.UserVerification
import com.powerly.user.data.model.VerificationBody
import com.powerly.core.network.KtorClient
import com.powerly.core.network.safeApiAction
import com.powerly.core.network.safeApiCall
import com.powerly.user.data.api.UserAuthApi
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single
internal class UserAuthRemoteDataSource(
    ktorClient: KtorClient,
) {
    private val client = ktorClient.client

    suspend fun emailCheck(email: String): ApiStatus<EmailCheck> = safeApiCall {
        client.post(UserAuthApi.AUTH_EMAIL_CHECK) {
            contentType(ContentType.Application.Json)
            setBody(EmailCheckBody(email))
        }.body<ApiResponse<EmailCheck?>>()
    }

    suspend fun emailLogin(
        email: String,
        password: String,
        deviceImei: String
    ): ApiStatus<User> = safeApiCall {
        client.post(UserAuthApi.AUTH_EMAIL_LOGIN) {
            contentType(ContentType.Application.Json)
            setBody(EmailLoginBody(email, password, deviceImei))
        }.body<ApiResponse<User?>>()
    }

    suspend fun emailRegister(
        email: String,
        password: String,
        countryId: Int,
        deviceImei: String
    ): ApiStatus<User?> = safeApiCall {
        client.post(UserAuthApi.AUTH_EMAIL_REGISTER) {
            contentType(ContentType.Application.Json)
            setBody(
                EmailRegisterBody(
                    email = email,
                    password = password,
                    countryId = countryId,
                    deviceImei = deviceImei
                )
            )
        }.body<ApiResponse<User?>>()
    }

    suspend fun emailVerify(code: String, email: String): ApiStatus<User> = safeApiCall {
        client.post(UserAuthApi.AUTH_EMAIL_VERIFY) {
            contentType(ContentType.Application.Json)
            setBody(VerificationBody(code, email))
        }.body<ApiResponse<User?>>()
    }

    suspend fun emailVerifyResend(email: String): ApiStatus<UserVerification> = safeApiCall {
        client.post(UserAuthApi.AUTH_EMAIL_VERIFY_RESEND) {
            contentType(ContentType.Application.Json)
            setBody(EmailVerifyResendBody(email))
        }.body<ApiResponse<UserVerification?>>()
    }

    suspend fun emailPasswordForget(email: String): ApiStatus<UserVerification> = safeApiCall {
        client.post(UserAuthApi.AUTH_PASSWORD_FORGET) {
            contentType(ContentType.Application.Json)
            setBody(EmailForgetBody(email))
        }.body<ApiResponse<UserVerification?>>()
    }

    suspend fun emailPasswordReset(
        pin: String,
        email: String,
        password: String
    ): ApiStatus<Boolean> = safeApiAction {
        client.post(UserAuthApi.AUTH_PASSWORD_RESET) {
            contentType(ContentType.Application.Json)
            setBody(
                EmailResetBody(
                    code = pin,
                    email = email,
                    password = password,
                    password2 = password
                )
            )
        }.body<ApiResponse<User?>>()
    }

    suspend fun emailPasswordResetResend(email: String): ApiStatus<UserVerification> = safeApiCall {
        client.post(UserAuthApi.AUTH_PASSWORD_RESET_RESEND) {
            contentType(ContentType.Application.Json)
            setBody(EmailVerifyResendBody(email))
        }.body<ApiResponse<UserVerification?>>()
    }
}
