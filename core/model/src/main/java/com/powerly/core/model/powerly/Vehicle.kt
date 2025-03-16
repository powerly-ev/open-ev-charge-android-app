package com.powerly.core.model.powerly

import com.powerly.core.model.api.BaseResponse
import com.powerly.core.model.user.User
import com.google.gson.annotations.SerializedName

class VehicleAddBody(
    @SerializedName("title") val title: String?,
    @SerializedName("vehicle_make_id") val vehicleMakeId: Int?,
    @SerializedName("vehicle_make_name") val vehicleMakeName: String?,
    @SerializedName("vehicle_model_id") val vehicleModelId: Int?,
    @SerializedName("year") val year: String?,
    @SerializedName("version") val version: String?,
    @SerializedName("color") val color: String?,
    @SerializedName("license_plate") val licensePlate: String?,
    @SerializedName("fuel_type") val fuelType: String?,
    @SerializedName("active") val active: String? = "1",
    @SerializedName("charging_connector_id") val chargingConnectorId: Int?,
)

class VehicleAddResponse : BaseResponse<Vehicle?>()
class VehiclesResponse : BaseResponse<List<Vehicle>?>()
class MakesResponse : BaseResponse<List<VehicleMaker>?>()
class ModelsResponse : BaseResponse<List<VehicleModel>?>()
class ConnectorsResponse : BaseResponse<List<Connector>?>()
class SourceTypesResponse : BaseResponse<List<SourceType>?>()
class AmenitiesResponse : BaseResponse<List<Amenity>?>()

data class Vehicle(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("year") var year: String? = null,
    @SerializedName("version") var version: String? = "",
    @SerializedName("color") var color: String? = null,
    @SerializedName("license_plate") var licensePlate: String? = "",
    @SerializedName("fuel_type") var fuelType: String? = null,
    @SerializedName("active") var active: String = "1",
    @SerializedName("owner") var owner: User? = null,
    @SerializedName("detail") var details: VehicleDetails? = null,
    @SerializedName("make") var maker: VehicleMaker? = null,
    @SerializedName("model") var model: VehicleModel? = null,
    @SerializedName("charging_connector") var connector: Connector? = null,
) {
    val isInitialized: Boolean get() = model != null && year != null && fuelType != null && maker != null
    var isActive: Boolean
        get() = active == "1"
        set(value) {
            active = if (value) "1" else "0"
        }
}

data class VehicleMaker(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("code") val code: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("logo") val logo: String? = null,
    @SerializedName("added_by") val addedBy: String? = null
)

data class VehicleModel(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("code") val code: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("logo") val logo: String? = null,
    @SerializedName("make") val make: VehicleMaker? = null
)

data class VehicleDetails(
    @SerializedName("battery_capacity") val batteryCapacity: String = "0",
    @SerializedName("charging_speed") val chargingSpeed: String = "0",
    @SerializedName("safety_rating") val safetyRating: String? = null,
    @SerializedName("mileage") val mileage: String = "0",
    @SerializedName("accident_history") val accidentHistory: String? = null,
    @SerializedName("fuel_efficiency") val fuelEfficiency: String? = null,
    @SerializedName("service_history") val serviceHistory: String? = null,
    @SerializedName("co2_emission") val co2Emission: String? = null,
    @SerializedName("extra_notes") val extraNotes: String? = null
)
