package com.powerly.core.domain.model.location

data class MyAddress(
    var addressLine1: String? = "",
    val addressLine2: String? = "",
    var addressLine3: String? = "",
    var city: String? = "",
    var state: String? = "",
    var zipcode: String? = "",
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
