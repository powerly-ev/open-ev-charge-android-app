package com.powerly.core.data.repositories

import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.user.EmailCheck
import com.powerly.core.model.user.EmailResetBody
import com.powerly.core.model.user.User
import com.powerly.core.model.user.UserVerification
import kotlinx.coroutines.flow.Flow

interface LoginEmailRepository {

    val isLoggedIn: Boolean

    val userFlow: Flow<User?>

    /**
     * Checks if an email exists.
     *
     * @param email The email to check.
     * @return  [ApiStatus] results containing the check result.
     */
    suspend fun emailCheck(email: String): ApiStatus<EmailCheck>

    /**
     * Logs in a user with their email and password.
     *
     * This function authenticates a user against the backend service.
     * On successful authentication, it returns the user's profile information.
     *
     * @param email the user's email.
     * @param password the user's password.
     * @return An [ApiStatus] which on success contains the logged-in [User] object.
     */
    suspend fun emailLogin(email: String, password: String): ApiStatus<User>

    /**
     * Registers a user with email.
     *
     * @param email the user's email.
     * @param password the user's password.
     * @param countryId the user's country id.
     * @return  [ApiStatus] results containing the registration result.
     */
    suspend fun emailRegister(
        email: String,
        password: String,
        countryId: Int
    ): ApiStatus<User?>

    /**
     * Verifies an email.
     *
     * @param code The verification code.
     * @param email The email to verify.
     * @return  [ApiStatus] results containing the verification result.
     */
    suspend fun emailVerify(code: String, email: String): ApiStatus<User>

    /**
     * Resends the email verification token.
     *
     * @param verificationToken The verification token to resend.
     * @return  [ApiStatus] results.
     */
    suspend fun emailVerifyResend(verificationToken: String): ApiStatus<UserVerification>

    /**
     * Initiates the email reset process.
     *
     * @param email The email to reset.
     * @return  [ApiStatus] results.
     */
    suspend fun emailForgetPassword(email: String): ApiStatus<UserVerification>

    /**
     * Verifies the email reset token.
     *
     * @param request The [EmailResetBody] containing verification details.
     * @return  [ApiStatus] results.
     */
    suspend fun emailResetPassword(request: EmailResetBody): ApiStatus<Boolean>

    /**
     * Resends the email reset token.
     *
     * @param email The email to resend verification code.
     * @return  [ApiStatus] results.
     */
    suspend fun emailResetResend(email: String): ApiStatus<UserVerification>

}