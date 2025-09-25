package com.powerly.core.data.repoImpl

import com.powerly.core.data.model.MakersStatus
import com.powerly.core.data.model.ModelsStatus
import com.powerly.core.data.repositories.VehiclesRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.Vehicle
import com.powerly.core.model.powerly.VehicleAddBody
import com.powerly.core.model.powerly.VehicleMaker
import com.powerly.core.network.RemoteDataSource
import com.powerly.core.network.asErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class VehiclesRepositoryImpl (
    private val remoteDataSource: RemoteDataSource,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : VehiclesRepository {

    override suspend fun vehiclesList() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.vehiclesList()
            if (response.hasData) ApiStatus.Success(response.getData.orEmpty())
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun vehicleUpdate(vehicle: Vehicle) = withContext(ioDispatcher) {
        try {
            val request = VehicleAddBody(
                title = vehicle.title ?: vehicle.maker?.name,
                vehicleMakeId = vehicle.maker?.id,
                vehicleMakeName = vehicle.maker?.name,
                vehicleModelId = vehicle.model?.id,
                active = vehicle.active,
                year = vehicle.year,
                version = vehicle.version,
                color = vehicle.color,
                licensePlate = vehicle.licensePlate,
                fuelType = vehicle.fuelType,
                chargingConnectorId = vehicle.connector?.id
            )

            //add new vehicle
            val response = if (vehicle.id == null) remoteDataSource.vehicleAdd(request)
            //update existing vehicle
            else remoteDataSource.vehicleUpdate(vehicle.id!!, request)

            if (response.hasData) ApiStatus.Success(true)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun vehicleDelete(vehicleId: Int) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.vehicleDelete(vehicleId)
            if (response.isSuccess) ApiStatus.Success(true)
            else ApiStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun vehiclesMakes() = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.vehiclesMakes()
            val makers = response.getData.orEmpty()
            val makersMap: Map<String, List<VehicleMaker>> = makers
                .sortedBy { it.name.lowercase() }
                .groupBy { it.name.first().toString() }

            if (response.hasData) MakersStatus.Success(makersMap)
            else MakersStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            e.printStackTrace()
            MakersStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            MakersStatus.Error(e.asErrorMessage)
        }
    }

    override suspend fun vehiclesModels(makeId: Int) = withContext(ioDispatcher) {
        try {
            val response = remoteDataSource.vehiclesModels(makeId)
            if (response.hasData)
                ModelsStatus.Success(response.getData.orEmpty().sortedBy { it.name })
            else ModelsStatus.Error(response.getMessage())
        } catch (e: HttpException) {
            e.printStackTrace()
            ModelsStatus.Error(e.asErrorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            ModelsStatus.Error(e.asErrorMessage)
        }
    }
}
