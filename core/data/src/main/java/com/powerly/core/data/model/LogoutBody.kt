package com.powerly.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LogoutBody(
    @SerialName("device_imei") private val imei: String
)
