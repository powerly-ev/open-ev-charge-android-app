package com.powerly.vehicles.data.repository

import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.api.map
import com.powerly.core.model.powerly.Vehicle
import com.powerly.core.model.powerly.VehicleMaker
import com.powerly.core.model.powerly.VehicleModel
import com.powerly.vehicles.data.datasource.remote.VehiclesRemoteDataSource
import com.powerly.vehicles.data.mapper.toAddBody
import com.powerly.vehicles.domain.repository.VehiclesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
internal class VehiclesRepositoryImpl(
    private val remoteDataSource: VehiclesRemoteDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher,
) : VehiclesRepository {

    override suspend fun vehiclesList(): ApiStatus<List<Vehicle>> =
        withContext(ioDispatcher) {
            remoteDataSource.vehiclesList()
        }

    override suspend fun vehicleUpdate(vehicle: Vehicle): ApiStatus<Boolean> =
        withContext(ioDispatcher) {
            val body = vehicle.toAddBody()
            val result = if (vehicle.id == null) remoteDataSource.vehicleAdd(body)
            else remoteDataSource.vehicleUpdate(vehicle.id!!, body)
            result.map { true }
        }

    override suspend fun vehicleDelete(vehicleId: Int): ApiStatus<Boolean> =
        withContext(ioDispatcher) {
            remoteDataSource.vehicleDelete(vehicleId)
        }

    override suspend fun vehiclesMakes(): ApiStatus<List<VehicleMaker>> =
        withContext(ioDispatcher) {
            remoteDataSource.vehiclesMakes()
        }

    override suspend fun vehiclesModels(makeId: Int): ApiStatus<List<VehicleModel>> =
        withContext(ioDispatcher) {
            remoteDataSource.vehiclesModels(makeId)
        }
}
