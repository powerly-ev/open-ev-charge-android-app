package com.powerly.core.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceBody(
    @SerialName("device_imei") private val deviceImei: String? = null,
    @SerialName("device_token") private val deviceToken: String? = null,
    @SerialName("device_type") private val deiceType: Int? = null,
    @SerialName("device_model") private val deviceModel: String? = null,
    @SerialName("device_version") private val deviceVersion: String? = null,
    @SerialName("app_version") private val appVersion: String? = null,
    @SerialName("lang") private val lang: String? = null
)

