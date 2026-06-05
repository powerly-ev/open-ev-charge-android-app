package com.powerly.core.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmailCheck(
    @SerialName("email_exists") private val emailExists: Int,
    @SerialName("require_verification") private val requireVerification: Int,
) {
    val isRegistered: Boolean get() = emailExists == 1
    val newUser: Boolean get() = emailExists == 0
    val isVerified: Boolean get() = requireVerification == 1
}

@Serializable
data class UserVerification(
    @SerialName("require_verification") val requireVerification: Int,
    @SerialName("verification_token") val verificationToken: String,
    @SerialName("sent_to") val sentTo: String,
    @SerialName("can_resend_in_seconds") val canResendInSeconds: Int,
    @SerialName("available_attempts") val availableAttempts: String,
)
