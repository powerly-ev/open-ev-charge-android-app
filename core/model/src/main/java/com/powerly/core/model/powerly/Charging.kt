package com.powerly.core.model.powerly

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class StartChargingBody(
    @SerialName("power_source_id") private val powersourceId: String,
    @SerialName("quantity") private val quantity: String,
    @SerialName("connector") private val connector: Int? = null
)

@Serializable
class StopChargingBody(
    @SerialName("order_id") private val orderId: String,
    @SerialName("power_source_id") private val powersourceId: String,
    @SerialName("connector") private val connector: Int? = null
)

