package com.powerly.core.data.model.location

import com.powerly.core.domain.model.location.MyAddress
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyAddressDto(
    @SerialName("address_line_1") val addressLine1: String? = "",
    @SerialName("address_line_2") val addressLine2: String? = "",
    @SerialName("address_line_3") val addressLine3: String? = "",
    @SerialName("city") val city: String? = "",
    @SerialName("state") val state: String? = "",
    @SerialName("zipcode") val zipcode: String? = "",
)

fun MyAddressDto.toDomain(): MyAddress = MyAddress(
    addressLine1 = addressLine1,
    addressLine2 = addressLine2,
    addressLine3 = addressLine3,
    city = city,
    state = state,
    zipcode = zipcode,
    countryCode = null
)
