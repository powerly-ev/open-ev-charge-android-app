package com.powerly.user.domain.model

data class EmailCheck(
    private val emailExists: Int,
    private val requireVerification: Int,
) {
    val isRegistered: Boolean get() = emailExists == 1
    val newUser: Boolean get() = emailExists == 0
    val isVerified: Boolean get() = requireVerification == 1
}

data class UserVerification(
    val requireVerification: Int,
    val verificationToken: String,
    val sentTo: String,
    val canResendInSeconds: Int,
    val availableAttempts: String,
)
