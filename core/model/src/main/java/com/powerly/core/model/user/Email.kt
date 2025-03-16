package com.powerly.core.model.user

import com.powerly.core.model.api.BaseResponse
import com.google.gson.annotations.SerializedName

/**
 * Email Check
 */

data class EmailCheckBody(
    @SerializedName("email") val email: String
)

class EmailCheckResponse : BaseResponse<EmailCheck?>()

data class EmailCheck(
    @SerializedName("email_exists") private val emailExists: Int,
    @SerializedName("require_verification") private val requireVerification: Int,
) {
    val isRegistered: Boolean get() = emailExists == 1
    val newUser: Boolean get() = emailExists == 0
    val isVerified: Boolean get() = requireVerification == 1
}

/**
 * Email Login
 */
data class EmailLoginBody(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("device_imei") val imei: String
)

class EmailLoginResponse : BaseResponse<User?>()

/**
 * Email Register
 */

data class EmailRegisterBody(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("country_id") val countryId: Int,
    @SerializedName("device_imei") val deviceImei: String,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null
)

class EmailRegisterResponse : BaseResponse<UserVerification?>()

data class UserVerification(
    @SerializedName("require_verification") val requireVerification: Int,
    @SerializedName("verification_token") val verificationToken: String,
    @SerializedName("sent_to") val sentTo: String,
    @SerializedName("can_resend_in_seconds") val canResendInSeconds: Int,
    @SerializedName("available_attempts") val availableAttempts: String,
)

/**
 * Email Verification
 */

data class VerificationBody(
    @SerializedName("code") val code: String,
    @SerializedName("email") val email: String
)

class AuthenticationResponse : BaseResponse<User?>()

/**
 * Email Resend Verification
 */

data class EmailVerifyResendBody(
    @SerializedName("email") val email: String
)

class EmailVerifyResendResponse : BaseResponse<UserVerification?>()

/**
 * Password reset
 */

data class EmailForgetBody(@SerializedName("email") val email: String)

class EmailForgetResponse : BaseResponse<UserVerification?>()


data class EmailResetBody(
    @SerializedName("code") val code: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("password_confirmation") val password2: String = password
)