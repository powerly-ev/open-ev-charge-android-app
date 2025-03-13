package com.powerly.core.model.user

import com.powerly.core.model.api.BaseResponse
import com.google.gson.annotations.SerializedName

class UpdateDeviceResponse : BaseResponse<Any?>()

data class DeviceBody(
    @SerializedName("device_imei") private val imei: String? = null,
    @SerializedName("device_token") private val token: String? = null,
    @SerializedName("device_type") private val deiceType: Int? = null,
    @SerializedName("device_model") private val deviceModel: String? = null,
    @SerializedName("device_version") private val deviceVersion: String? = null,
    @SerializedName("app_version") private val appVersion: String? = null,
    @SerializedName("lang") private val lang: String? = null
)

