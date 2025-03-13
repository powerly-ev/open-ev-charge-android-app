package com.powerly.core.model.user

import com.google.gson.annotations.SerializedName

/**
 * Data class representing the request body for social media login.
 *
 * @property jwtToken Token id of the social media account login.
 * @property deviceToken Device Messaging token for push notifications.
 * @property deviceImei Device IMEI for push notifications.
 * @property countryId User Country Id.

 */
data class SocialLoginBody(
    @SerializedName("jwt_token") val jwtToken: String,
    @SerializedName("device_token") val deviceToken: String,
    @SerializedName("device_imei") val deviceImei: String,
    @SerializedName("country_id") val countryId: Int?
)