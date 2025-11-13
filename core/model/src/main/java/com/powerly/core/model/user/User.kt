package com.powerly.core.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id") val id: Int = -1,
    @SerialName("first_name") val firstName: String = "",
    @SerialName("last_name") val lastName: String = "",
    @SerialName("currency") val appCurrency: String? = null,
    @SerialName("balance") var balance: Double = 0.0,

    @SerialName("country_id") val countryId: Int? = null,

    @SerialName("email") val email: String = "",
    @SerialName("email_verified") val emailVerified: Int = 0,

    @SerialName("vat_id") val vatId: String? = null,
    @SerialName("access_token") var accessToken: String? = null
) {
    val fullName: String get() = "$firstName $lastName"
    val currency: String get() = appCurrency.orEmpty()
}


@Serializable
data class UserUpdateBody(
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("password") val password: String? = null,
    @SerialName("vat_id") val vatId: String? = null,
    @SerialName("country_id") val countryId: Int? = null,
    @SerialName("currency") val currency: String? = null,
    @SerialName("latitude") val latitude: Double? = null,
    @SerialName("longitude") val longitude: Double? = null
)


@Serializable
data class RefreshToken(
    @SerialName("access_token") val accessToken: String? = ""
)

@Serializable
data class LogoutBody(
    @SerialName("device_imei")
    private val imei: String
)
