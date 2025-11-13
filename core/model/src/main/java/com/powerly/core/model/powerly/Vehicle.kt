package com.powerly.core.model.powerly

import com.powerly.core.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class VehicleAddBody(
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

@Serializable
data class Vehicle(
    @SerialName("id") val id: Int? = null,
    @SerialName("title") var title: String? = null,
    @SerialName("year") var year: String? = null,
    @SerialName("version") var version: String? = "",
    @SerialName("color") var color: String? = null,
    @SerialName("license_plate") var licensePlate: String? = "",
    @SerialName("fuel_type") var fuelType: String? = null,
    @SerialName("active") var active: String = "1",
    @SerialName("owner") var owner: User? = null,
    @SerialName("detail") var details: VehicleDetails? = null,
    @SerialName("make") var maker: VehicleMaker? = null,
    @SerialName("model") var model: VehicleModel? = null,
    @SerialName("charging_connector") var connector: Connector? = null,
) {
    val isInitialized: Boolean get() = model != null && year != null && fuelType != null && maker != null
    var isActive: Boolean
        get() = active == "1"
        set(value) {
            active = if (value) "1" else "0"
        }
}

@Serializable
data class VehicleMaker(
    @SerialName("id") val id: Int = 0,
    @SerialName("code") val code: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("logo") val logo: String? = null,
    @SerialName("added_by") val addedBy: String? = null
)

@Serializable
data class VehicleModel(
    @SerialName("id") val id: Int = 0,
    @SerialName("code") val code: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("logo") val logo: String? = null,
    @SerialName("make") val make: VehicleMaker? = null
)

@Serializable
data class VehicleDetails(
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
