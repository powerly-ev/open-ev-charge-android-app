package com.powerly.vehicles.data.model.dto

import com.powerly.core.data.model.powerly.ConnectorDto
import com.powerly.core.data.model.powerly.toDomain
import com.powerly.core.domain.model.powerly.Vehicle
import com.powerly.core.domain.model.powerly.VehicleDetails
import com.powerly.core.domain.model.powerly.VehicleMaker
import com.powerly.core.domain.model.powerly.VehicleModel
import com.powerly.core.domain.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class VehicleDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("year") val year: String? = null,
    @SerialName("version") val version: String? = "",
    @SerialName("color") val color: String? = null,
    @SerialName("license_plate") val licensePlate: String? = "",
    @SerialName("fuel_type") val fuelType: String? = null,
    @SerialName("active") val active: String = "1",
    @SerialName("owner") val owner: VehicleOwnerDto? = null,
    @SerialName("detail") val details: VehicleDetailsDto? = null,
    @SerialName("make") val maker: VehicleMakerDto? = null,
    @SerialName("model") val model: VehicleModelDto? = null,
    @SerialName("charging_connector") val connector: ConnectorDto? = null,
)

@Serializable
internal data class VehicleOwnerDto(
    @SerialName("id") val id: Int = -1,
    @SerialName("first_name") val firstName: String = "",
    @SerialName("last_name") val lastName: String = "",
    @SerialName("email") val email: String = ""
)

@Serializable
internal data class VehicleMakerDto(
    @SerialName("id") val id: Int = 0,
    @SerialName("code") val code: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("logo") val logo: String? = null,
    @SerialName("added_by") val addedBy: String? = null
)

@Serializable
internal data class VehicleModelDto(
    @SerialName("id") val id: Int = 0,
    @SerialName("code") val code: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("logo") val logo: String? = null,
    @SerialName("make") val make: VehicleMakerDto? = null
)

@Serializable
internal data class VehicleDetailsDto(
    @SerialName("battery_capacity") val batteryCapacity: String = "0",
    @SerialName("charging_speed") val chargingSpeed: String = "0",
    @SerialName("safety_rating") val safetyRating: String? = null,
    @SerialName("mileage") val mileage: String = "0",
    @SerialName("accident_history") val accidentHistory: String? = null,
    @SerialName("fuel_efficiency") val fuelEfficiency: String? = null,
    @SerialName("service_history") val serviceHistory: String? = null,
    @SerialName("co2_emission") val co2Emission: String? = null,
    @SerialName("extra_notes") val extraNotes: String? = null
)

internal fun VehicleDto.toDomain(): Vehicle = Vehicle(
    id = id,
    title = title,
    year = year,
    version = version,
    color = color,
    licensePlate = licensePlate,
    fuelType = fuelType,
    active = active,
    owner = owner?.toDomain(),
    details = details?.toDomain(),
    maker = maker?.toDomain(),
    model = model?.toDomain(),
    connector = connector?.toDomain()
)

internal fun VehicleOwnerDto.toDomain(): User = User(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email
)

internal fun VehicleMakerDto.toDomain(): VehicleMaker = VehicleMaker(
    id = id,
    code = code,
    name = name,
    logo = logo,
    addedBy = addedBy
)

internal fun VehicleModelDto.toDomain(): VehicleModel = VehicleModel(
    id = id,
    code = code,
    name = name,
    logo = logo,
    make = make?.toDomain()
)

internal fun VehicleDetailsDto.toDomain(): VehicleDetails = VehicleDetails(
    batteryCapacity = batteryCapacity,
    chargingSpeed = chargingSpeed,
    safetyRating = safetyRating,
    mileage = mileage,
    accidentHistory = accidentHistory,
    fuelEfficiency = fuelEfficiency,
    serviceHistory = serviceHistory,
    co2Emission = co2Emission,
    extraNotes = extraNotes
)
