package com.powerly.vehicles.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class VehicleAddBody(
    @SerialName("title") val title: String?,
    @SerialName("vehicle_make_id") val vehicleMakeId: Int?,
    @SerialName("vehicle_make_name") val vehicleMakeName: String?,
    @SerialName("vehicle_model_id") val vehicleModelId: Int?,
    @SerialName("year") val year: String?,
    @SerialName("version") val version: String?,
    @SerialName("color") val color: String?,
    @SerialName("license_plate") val licensePlate: String?,
    @SerialName("fuel_type") val fuelType: String?,
    @SerialName("active") val active: String? = "1",
    @SerialName("charging_connector_id") val chargingConnectorId: Int?,
)
