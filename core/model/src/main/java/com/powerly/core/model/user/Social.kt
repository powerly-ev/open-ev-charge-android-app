package com.powerly.core.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class representing the request body for social media login.
 *
 * @property jwtToken Token id of the social media account login.
 * @property deviceToken Device Messaging token for push notifications.
 * @property deviceImei Device IMEI for push notifications.
 * @property countryId User Country Id.

 */
@Serializable
data class SocialLoginBody(
    @SerialName("jwt_token") val jwtToken: String,
    @SerialName("device_token") val deviceToken: String,
    @SerialName("device_imei") val deviceImei: String,
    @SerialName("country_id") val countryId: Int?
)