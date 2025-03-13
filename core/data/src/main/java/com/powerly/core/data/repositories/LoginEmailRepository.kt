package com.powerly.core.data.repositories

import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.user.EmailCheck
import com.powerly.core.model.user.EmailLoginBody
import com.powerly.core.model.user.EmailRegisterBody
import com.powerly.core.model.user.EmailResetBody
import com.powerly.core.model.user.User
import com.powerly.core.model.user.UserVerification
import com.powerly.core.model.user.VerificationBody

interface LoginEmailRepository {

    /**
     * Checks if an email exists.
     *
     * @param email The email to check.
     * @return  [ApiStatus] results containing the check result.
     */
    suspend fun emailCheck(email: String): ApiStatus<EmailCheck>

    /**
     * Logs in a user with email.
     *
     * @param request The [EmailLoginBody] containing login details.
     * @return  [ApiStatus] results containing the login result.
     */
    suspend fun emailLogin(request: EmailLoginBody): ApiStatus<User>

    /**
     * Registers a user with email.
     *
     * @param request The [EmailRegisterBody] containing registration details.
     * @return  [ApiStatus] results containing the registration result.
     */
    suspend fun emailRegister(request: EmailRegisterBody): ApiStatus<UserVerification>

    /**
     * Verifies an email.
     *
     * @param request The [VerificationBody] containing verification details.
     * @return  [ApiStatus] results containing the verification result.
     */
    suspend fun emailVerify(request: VerificationBody): ApiStatus<User>

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