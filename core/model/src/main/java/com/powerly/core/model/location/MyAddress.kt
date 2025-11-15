package com.powerly.core.model.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyAddress(
    @SerialName("address_line_1") var addressLine1: String? = "",
    @SerialName("address_line_2") val addressLine2: String? = "",
    @SerialName("address_line_3") var addressLine3: String? = "",
    @SerialName("city") var city: String? = "",
    @SerialName("state") var state: String? = "",
    @SerialName("zipcode") var zipcode: String? = "",
    var countryCode: String? = null
) {
    override fun toString(): String = addressLine1.orEmpty()
    val detailedAddress: String
        get() {
            val address = addressLine1.orEmpty().trim()
            val city = city.orEmpty().trim()
            val state = state.orEmpty().trim()
            val zipcode = zipcode.orEmpty().trim()
            return "$address, $city, $state, $zipcode".replace(", ,", ", ")
        }
}
