package com.powerly.vehicles.data.api

internal object VehiclesApi {
    const val VEHICLES = "vehicles"
    const val VEHICLES_ACTION = "vehicles/{id}"
    const val VEHICLE_MAKES = "vehicles/makes"
    const val VEHICLE_MODELS = "vehicles/models/{make_id}"

    fun vehicleAction(id: Int): String = VEHICLES_ACTION.replace("{id}", id.toString())
    fun vehicleModels(makeId: Int): String = VEHICLE_MODELS.replace("{make_id}", makeId.toString())
}
