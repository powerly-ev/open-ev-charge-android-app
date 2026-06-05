package com.powerly.orders.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class StopChargingBody(
    @SerialName("order_id") private val orderId: String,
    @SerialName("power_source_id") private val powersourceId: String,
    @SerialName("connector") private val connector: Int? = null
)
