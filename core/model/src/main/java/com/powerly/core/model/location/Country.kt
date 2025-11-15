package com.powerly.core.model.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Country(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String = "",
    @SerialName("iso") val iso: String = ""
)

@Serializable
data class AppCurrency(
    @SerialName("currency_iso") val iso: String,
    @SerialName("id") val id: Int = 0,
)