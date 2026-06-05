package com.powerly.user.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmailCheckBody(
    @SerialName("email") val email: String
)

@Serializable
data class EmailLoginBody(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("device_imei") val imei: String
)

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
data class VerificationBody(
    @SerialName("code") val code: String,
    @SerialName("email") val email: String
)

@Serializable
data class EmailVerifyResendBody(
    @SerialName("email") val email: String
)

@Serializable
data class EmailForgetBody(@SerialName("email") val email: String)

@Serializable
data class EmailResetBody(
    @SerialName("code") val code: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("password_confirmation") val password2: String
)
