package com.powerly.core.model.location

import com.google.gson.annotations.SerializedName

data class MyAddress(
    @SerializedName("address_line_1") var addressLine1: String? = "",
    @SerializedName("address_line_2") val addressLine2: String? = "",
    @SerializedName("address_line_3") var addressLine3: String? = "",
    @SerializedName("city") var city: String? = "",
    @SerializedName("state") var state: String? = "",
    @SerializedName("zipcode") var zipcode: String? = "",
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
