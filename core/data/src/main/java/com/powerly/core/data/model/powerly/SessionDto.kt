package com.powerly.core.data.model.powerly

import com.powerly.core.domain.model.powerly.Price
import com.powerly.core.domain.model.powerly.Session
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class SessionDto(
    @SerialName("id") val id: String = "",
    @SerialName("connector_number") val connectorNumber: Int? = null,
    @SerialName("status") val status: Int = 0,
    @SerialName("price") val price: Double = 0.0,
    @SerialName("prices") val prices: List<PriceDto>? = null,
    @SerialName("unit_price") val unitPrice: Double = 0.0,
    @SerialName("app_fees") val appFees: Double = 0.0,
    @SerialName("earning") val earning: Double = 0.0,
    @SerialName("fees") val fees: Double = 0.0,
    @SerialName("charging_session_time") val chargingSessionTime: Double = 0.0,
    @SerialName("charging_session_energy") val chargingSessionEnergy: Double = 0.0,
    @OptIn(ExperimentalSerializationApi::class)
    @JsonNames("price_unit", "unit") val priceUnit: String = "minutes",
    @SerialName("quantity") val quantity: String? = null,
    @SerialName("requested_quantity") val quantityRequested: String = "",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("delivery_date") val deliveryAt: String? = null,
    @SerialName("reserved_at") val reservedAt: String = "",
    @SerialName("charge_point") val chargePoint: PowerSourceDto = PowerSourceDto(),
    @SerialName("currency") val currency: String = ""
)

@Serializable
data class PriceDto(
    @SerialName("id") val id: Int = 0,
    @SerialName("quantity") val quantity: Double = 0.0,
    @SerialName("price") val price: Double = 0.0,
    @SerialName("total") val total: Double = 0.0,
    @SerialName("endingTime") val endingTime: String = "",
    @SerialName("startingTime") val startingTime: String = "",
)

fun SessionDto.toDomain(): Session = Session(
    id = id,
    connectorNumber = connectorNumber,
    status = status,
    price = price,
    prices = prices?.map { it.toDomain() },
    unitPrice = unitPrice,
    appFees = appFees,
    earning = earning,
    fees = fees,
    chargingSessionTime = chargingSessionTime,
    chargingSessionEnergy = chargingSessionEnergy,
    priceUnit = priceUnit,
    quantity = quantity,
    quantityRequested = quantityRequested,
    createdAt = createdAt,
    deliveryAt = deliveryAt,
    reservedAt = reservedAt,
    chargePoint = chargePoint.toDomain(),
    currency = currency
)

fun PriceDto.toDomain(): Price = Price(
    id = id,
    quantity = quantity,
    price = price,
    total = total,
    endingTime = endingTime,
    startingTime = startingTime
)
