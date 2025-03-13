package com.powerly.core.data.repositories

import com.powerly.core.data.model.MakersStatus
import com.powerly.core.data.model.ModelsStatus
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.Vehicle

interface VehiclesRepository {

    /**
     * Retrieves a list of vehicles.
     *
     * @return  [ApiStatus] results containing the list of vehicles.
     */
    suspend fun vehiclesList(): ApiStatus<List<Vehicle>>

    /**
     * Updates a vehicle.
     *
     * @param vehicle The [Vehicle] object containing updated information.
     * @return  [ApiStatus] results indicating the success or failure of the update.
     */
    suspend fun vehicleUpdate(vehicle: Vehicle): ApiStatus<Boolean>

    /**
     * Deletes a vehicle.
     *
     * @param vehicleId The ID of the vehicle to delete.
     * @return  [ApiStatus] results indicating the success or failure of the deletion.
     */
    suspend fun vehicleDelete(vehicleId: Int): ApiStatus<Boolean>

    /**
     * Retrieves a list of vehicle makes.
     *
     * @return  [com.powerly.core.data.model.MakersStatus] results containing the list of vehicle makes.
     */
    suspend fun vehiclesMakes(): MakersStatus

    /**
     * Retrieves a list of vehicle models for a given make.
     *
     * @param makeId The ID of the vehicle make.
     * @return  [com.powerly.core.data.model.ModelsStatus] results containing the list of vehicle models.
     */
    suspend fun vehiclesModels(makeId: Int): ModelsStatus
}

