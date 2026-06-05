package com.powerly.vehicles.domain.repository

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.model.powerly.Vehicle
import com.powerly.core.model.powerly.VehicleMaker
import com.powerly.core.model.powerly.VehicleModel

interface VehiclesRepository {

    suspend fun vehiclesList(): ApiStatus<List<Vehicle>>

    /** Adds a new vehicle when [Vehicle.id] is null, otherwise updates it. */
    suspend fun vehicleUpdate(vehicle: Vehicle): ApiStatus<Boolean>

    suspend fun vehicleDelete(vehicleId: Int): ApiStatus<Boolean>

    suspend fun vehiclesMakes(): ApiStatus<List<VehicleMaker>>

    suspend fun vehiclesModels(makeId: Int): ApiStatus<List<VehicleModel>>
}
