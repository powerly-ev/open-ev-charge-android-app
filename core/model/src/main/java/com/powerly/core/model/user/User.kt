package com.powerly.core.model.user

import com.powerly.core.model.api.BaseResponse
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Int = -1,
    @SerializedName("first_name") val firstName: String = "",
    @SerializedName("last_name") val lastName: String = "",
    @SerializedName("currency") val appCurrency: String? = null,
    @SerializedName("balance") var balance: Double = 0.0,

    @SerializedName("country_id") val countryId: Int? = null,

    @SerializedName("email") val email: String = "",
    @SerializedName("email_verified") val emailVerified: Int = 0,

    @SerializedName("vat_id") val vatId: String? = null,
    @SerializedName("access_token") var accessToken: String? = null
) {
    val fullName: String get() = "$firstName $lastName"
    val currency: String get() = appCurrency.orEmpty()
}


class UserResponse : BaseResponse<User>()

data class UserUpdateBody(
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("vat_id") val vatId: String? = null,
    @SerializedName("country_id") val countryId: Int? = null,
    @SerializedName("currency") val currency: String? = null,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null
)

class RefreshTokenResponse : BaseResponse<RefreshToken>()

data class RefreshToken(
    @SerializedName("access_token") val accessToken: String? = ""
)

data class LogoutBody(
    @SerializedName("device_imei")
    private val imei: String
)
