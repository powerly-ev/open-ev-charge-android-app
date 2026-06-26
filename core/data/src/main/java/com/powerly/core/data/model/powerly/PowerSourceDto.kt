package com.powerly.core.data.model.powerly

import com.powerly.core.data.model.location.MyAddressDto
import com.powerly.core.data.model.location.toDomain
import com.powerly.core.domain.model.location.MyAddress
import com.powerly.core.domain.model.powerly.Amenity
import com.powerly.core.domain.model.powerly.Connector
import com.powerly.core.domain.model.powerly.Pivot
import com.powerly.core.domain.model.powerly.PowerSource
import com.powerly.core.domain.model.powerly.SourceType
import com.powerly.core.network.api.FlexibleBooleanSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PowerSourceDto(
    @SerialName("id") val id: String = "",
    @SerialName("identifier") val identifier: String = "",
    @SerialName("token") val token: String? = null,
    @SerialName("category") val category: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("description") val description: String? = null,
    @SerialName("latitude") val latitude: Double? = null,
    @SerialName("longitude") val longitude: Double? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("session_limit_type") val sessionLimitType: String? = null,
    @SerialName("session_limit_value") val sessionLimit: Int? = 0,
    @SerialName("contact_number") val contactNumber: String? = null,
    @SerialName("open_time") val openTime: String? = null,
    @SerialName("close_time") val closeTime: String? = null,
    @SerialName("type") val sourceType: SourceTypeDto? = null,
    @SerialName("price") val price: Double = 0.0,
    @SerialName("price_unit") val priceUnit: String? = null,
    @SerialName("rating") val rating: Double = 0.0,
    @SerialName("online_status") val onlineStatus: Int = 0,
    @SerialName("is_in_use")
    @Serializable(with = FlexibleBooleanSerializer::class)
    val inUse: Boolean = false,
    @SerialName("is_reserved")
    @Serializable(with = FlexibleBooleanSerializer::class)
    val reserved: Boolean = false,
    @SerialName("used_by_current_user")
    @Serializable(with = FlexibleBooleanSerializer::class)
    val isInUseByYou: Boolean = false,
    @SerialName("booked_by_current_user")
    @Serializable(with = FlexibleBooleanSerializer::class)
    val isReservedByYou: Boolean = false,
    @SerialName("listed")
    @Serializable(with = FlexibleBooleanSerializer::class)
    val listed: Boolean = true,
    @SerialName("connectors") val connectors: List<ConnectorDto>? = null,
    @SerialName("amenities") val amenities: List<AmenityDto>? = null,
    @SerialName("address") val address: MyAddressDto? = null,
    @SerialName("external") val isExternal: Boolean = false,
    @SerialName("media") val media: List<MediaDto> = listOf(),
    @SerialName("distance") val distance: Double? = null,
    @SerialName("currency") val currency: String = ""
)

@Serializable
data class ConnectorDto(
    @SerialName("id") val id: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("number") val number: Int = 1,
    @SerialName("icon") val icon: String = "",
    @SerialName("status") val status: com.powerly.core.domain.model.powerly.ConnectorStatus =
        com.powerly.core.domain.model.powerly.ConnectorStatus.Available,
    @SerialName("localized_status") val statusLabel: String = status.name,
    @SerialName("type") val type: String? = "",
    @SerialName("max_power") val maxPower: Double = 0.0,
)

@Serializable
data class PivotDto(
    @SerialName("charge_point_id") val chargePointId: String,
    @SerialName("connector_id") val connectorId: String,
    @SerialName("order_id") val orderId: String? = null
)

@Serializable
data class AmenityDto(
    @SerialName("id") val id: Int = -1,
    @SerialName("name") val name: String = "",
    @SerialName("icon") val icon: String? = "",
    @SerialName("description") val description: String? = "",
    @SerialName("pivot") val pivot: PivotDto? = null
)

@Serializable
data class SourceTypeDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String = "",
    @SerialName("img") val img: String = "",
    @SerialName("current_type") val currentType: String = "",
    @SerialName("max_power") val maxPower: Double = 0.0
)

fun PowerSourceDto.toDomain(): PowerSource = PowerSource(
    id = id,
    identifier = identifier,
    token = token,
    category = category,
    title = title,
    description = description,
    latitude = latitude,
    longitude = longitude,
    image = image,
    sessionLimitType = sessionLimitType,
    sessionLimit = sessionLimit,
    contactNumber = contactNumber,
    _openTime = openTime,
    _closeTime = closeTime,
    sourceType = sourceType?.toDomain(),
    price = price,
    priceUnit = priceUnit,
    rating = rating,
    onlineStatus = onlineStatus,
    inUse = inUse,
    reserved = reserved,
    isInUseByYou = isInUseByYou,
    isReservedByYou = isReservedByYou,
    listed = listed,
    connectors = connectors?.map { it.toDomain() },
    amenities = amenities?.map { it.toDomain() },
    address = address?.toDomain(),
    isExternal = isExternal,
    media = media.map { it.toDomain() },
    distance = distance,
    currency = currency
)

fun ConnectorDto.toDomain(): Connector = Connector(
    id = id,
    name = name,
    number = number,
    icon = icon,
    status = status,
    statusLabel = statusLabel,
    type = type,
    maxPower = maxPower
)

fun PivotDto.toDomain(): Pivot = Pivot(
    chargePointId = chargePointId,
    connectorId = connectorId,
    orderId = orderId
)

fun AmenityDto.toDomain(): Amenity = Amenity(
    id = id,
    name = name,
    icon = icon,
    description = description,
    pivot = pivot?.toDomain()
)

fun SourceTypeDto.toDomain(): SourceType = SourceType(
    id = id,
    name = name,
    img = img,
    currentType = currentType,
    maxPower = maxPower
)
