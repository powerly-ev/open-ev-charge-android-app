package com.powerly.e2e.fakes

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.user.User
import com.powerly.user.domain.model.EmailCheck
import com.powerly.user.domain.model.UserVerification
import com.powerly.user.domain.repository.LoginEmailRepository
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Deterministic [LoginEmailRepository] for E2E. By default, the email is an existing,
 * verified account, and login/register/verify all succeed. Tweak [emailExists] to
 * drive the new-user (register) path.
 */
class FakeLoginEmailRepository : LoginEmailRepository {

    /** 1 = existing user (login path), 0 = new user (register path). */
    var emailExists: Int = 1

    override val userFlow = MutableStateFlow<User?>(null)

    private fun verification() = UserVerification(
        requireVerification = 1,
        verificationToken = "token",
        sentTo = "jane@example.com",
        canResendInSeconds = 30,
        availableAttempts = "3",
    )

    override suspend fun emailCheck(email: String): ApiStatus<EmailCheck> =
        ApiStatus.Success(EmailCheck(emailExists = emailExists, requireVerification = 1))

    override suspend fun emailLogin(email: String, password: String): ApiStatus<User> =
        ApiStatus.Success(TestData.user)

    override suspend fun emailRegister(email: String, password: String, countryId: Int): ApiStatus<User?> =
        ApiStatus.Success(TestData.user)

    override suspend fun emailVerify(code: String, email: String): ApiStatus<User> =
        ApiStatus.Success(TestData.user)

    override suspend fun emailVerifyResend(verificationToken: String): ApiStatus<UserVerification> =
        ApiStatus.Success(verification())

    override suspend fun emailForgetPassword(email: String): ApiStatus<UserVerification> =
        ApiStatus.Success(verification())

    override suspend fun emailResetPassword(pin: String, email: String, password: String): ApiStatus<Boolean> =
        ApiStatus.Success(true)

    override suspend fun emailResetResend(email: String): ApiStatus<UserVerification> =
        ApiStatus.Success(verification())
}
