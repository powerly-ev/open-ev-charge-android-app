package com.powerly.core.data.model.location

import com.powerly.core.domain.model.location.Country
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CountryDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String = "",
    @SerialName("iso") val iso: String = ""
)

internal fun CountryDto.toDomain(): Country = Country(
    id = id,
    name = name,
    iso = iso
)
