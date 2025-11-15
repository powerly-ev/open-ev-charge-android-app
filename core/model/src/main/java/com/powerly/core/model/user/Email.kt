package com.powerly.core.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Email Check
 */
@Serializable
data class EmailCheckBody(
    @SerialName("email") val email: String
)


@Serializable
data class EmailCheck(
    @SerialName("email_exists") private val emailExists: Int,
    @SerialName("require_verification") private val requireVerification: Int,
) {
    val isRegistered: Boolean get() = emailExists == 1
    val newUser: Boolean get() = emailExists == 0
    val isVerified: Boolean get() = requireVerification == 1
}

/**
 * Email Login
 */
@Serializable
data class EmailLoginBody(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("device_imei") val imei: String
)


/**
 * Email Register
 */

@Serializable
data class EmailRegisterBody(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("country_id") val countryId: Int,
    @SerialName("device_imei") val deviceImei: String,
    @SerialName("latitude") val latitude: Double? = null,
    @SerialName("longitude") val longitude: Double? = null
)

@Serializable
data class UserVerification(
    @SerialName("require_verification") val requireVerification: Int,
    @SerialName("verification_token") val verificationToken: String,
    @SerialName("sent_to") val sentTo: String,
    @SerialName("can_resend_in_seconds") val canResendInSeconds: Int,
    @SerialName("available_attempts") val availableAttempts: String,
)

/**
 * Email Verification
 */

@Serializable
data class VerificationBody(
    @SerialName("code") val code: String,
    @SerialName("email") val email: String
)

/**
 * Email Resend Verification
 */

@Serializable
data class EmailVerifyResendBody(
    @SerialName("email") val email: String
)

/**
 * Password reset
 */

@Serializable
data class EmailForgetBody(@SerialName("email") val email: String)

@Serializable
data class EmailResetBody(
    @SerialName("code") val code: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("password_confirmation") val password2: String
)