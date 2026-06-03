package com.powerly.user.domain.use_case

import com.powerly.core.model.api.ApiErrorConstants
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.util.Message
import com.powerly.user.domain.model.LoginResult
import com.powerly.user.domain.repository.LoginEmailRepository
import org.koin.core.annotation.Single

/**
 * Authenticates a user with email + password. When the backend reports the
 * account is unverified, transparently requests a fresh verification code and
 * surfaces [LoginResult.VERIFICATION_REQUIRED] so the UI can route to the
 * verification step.
 */
@Single
class EmailLoginUseCase(
    private val repository: LoginEmailRepository
) {
    data class Result(val outcome: LoginResult, val error: Message? = null)

    suspend operator fun invoke(email: String, password: String): Result {
        return when (val result = repository.emailLogin(email, password)) {
            is ApiStatus.Success -> Result(LoginResult.SUCCESS)
            is ApiStatus.Error -> {
                if (result.msg.code == ApiErrorConstants.UNAUTHENTICATED) {
                    when (val resend = repository.emailVerifyResend(email)) {
                        is ApiStatus.Success -> Result(LoginResult.VERIFICATION_REQUIRED, result.msg)
                        is ApiStatus.Error -> Result(LoginResult.ERROR, resend.msg)
                        else -> Result(LoginResult.ERROR, result.msg)
                    }
                } else {
                    Result(LoginResult.ERROR, result.msg)
                }
            }

            else -> Result(LoginResult.ERROR)
        }
    }
}
