package com.powerly.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserUpdateBody(
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
