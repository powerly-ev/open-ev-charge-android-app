package com.powerly.core.domain.model.powerly

import com.powerly.core.domain.model.user.User

data class Vehicle(
    val id: Int? = null,
    var title: String? = null,
    var year: String? = null,
    var version: String? = "",
    var color: String? = null,
    var licensePlate: String? = "",
    var fuelType: String? = null,
    var active: String = "1",
    var owner: User? = null,
    var details: VehicleDetails? = null,
    var maker: VehicleMaker? = null,
    var model: VehicleModel? = null,
    var connector: Connector? = null,
) {
    val isInitialized: Boolean
        get() = model != null && year != null && fuelType != null && maker != null
    var isActive: Boolean
        get() = active == "1"
        set(value) {
            active = if (value) "1" else "0"
        }
}

data class VehicleMaker(
    val id: Int = 0,
    val code: String = "",
    val name: String = "",
    val logo: String? = null,
    val addedBy: String? = null
)

data class VehicleModel(
    val id: Int = 0,
    val code: String = "",
    val name: String = "",
    val logo: String? = null,
    val make: VehicleMaker? = null
)

data class VehicleDetails(
    val batteryCapacity: String = "0",
    val chargingSpeed: String = "0",
    val safetyRating: String? = null,
    val mileage: String = "0",
    val accidentHistory: String? = null,
    val fuelEfficiency: String? = null,
    val serviceHistory: String? = null,
    val co2Emission: String? = null,
    val extraNotes: String? = null
)
