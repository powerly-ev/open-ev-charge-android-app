package com.powerly.powersource.charging.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewBody(
    @SerialName("rating") val rating: Double,
    @SerialName("content") val msg: String,
)
