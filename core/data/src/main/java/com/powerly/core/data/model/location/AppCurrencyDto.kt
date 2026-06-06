package com.powerly.core.data.model.location

import com.powerly.core.domain.model.location.AppCurrency
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AppCurrencyDto(
    @SerialName("currency_iso") val iso: String,
    @SerialName("id") val id: Int = 0,
)

internal fun AppCurrencyDto.toDomain(): AppCurrency = AppCurrency(
    iso = iso,
    id = id
)
