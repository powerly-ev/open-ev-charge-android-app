package com.powerly.user.data.model

import com.powerly.user.domain.model.UserVerification
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserVerificationDto(
    @SerialName("require_verification") val requireVerification: Int,
    @SerialName("verification_token") val verificationToken: String,
    @SerialName("sent_to") val sentTo: String,
    @SerialName("can_resend_in_seconds") val canResendInSeconds: Int,
    @SerialName("available_attempts") val availableAttempts: String,
)

internal fun UserVerificationDto.toDomain(): UserVerification = UserVerification(
    requireVerification = requireVerification,
    verificationToken = verificationToken,
    sentTo = sentTo,
    canResendInSeconds = canResendInSeconds,
    availableAttempts = availableAttempts
)
