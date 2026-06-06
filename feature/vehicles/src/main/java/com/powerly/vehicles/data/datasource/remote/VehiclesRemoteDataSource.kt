package com.powerly.vehicles.data.datasource.remote

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.map
import com.powerly.core.domain.model.powerly.Vehicle
import com.powerly.core.domain.model.powerly.VehicleMaker
import com.powerly.core.domain.model.powerly.VehicleModel
import com.powerly.core.network.KtorClient
import com.powerly.core.network.api.ApiResponse
import com.powerly.core.network.safeApiAction
import com.powerly.core.network.safeApiCall
import com.powerly.vehicles.data.api.VehiclesApi
import com.powerly.vehicles.data.model.VehicleAddBody
import com.powerly.vehicles.data.model.dto.VehicleDto
import com.powerly.vehicles.data.model.dto.VehicleMakerDto
import com.powerly.vehicles.data.model.dto.VehicleModelDto
import com.powerly.vehicles.data.model.dto.toDomain
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single
internal class VehiclesRemoteDataSource(
    ktorClient: KtorClient,
) {
    private val client = ktorClient.client

    suspend fun vehicleAdd(request: VehicleAddBody): ApiStatus<Vehicle> = safeApiCall<VehicleDto> {
        client.post(VehiclesApi.VEHICLES) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }.map { it.toDomain() }

    suspend fun vehicleUpdate(id: Int, request: VehicleAddBody): ApiStatus<Vehicle> =
        safeApiCall<VehicleDto> {
            client.put(VehiclesApi.vehicleAction(id)) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        }.map { it.toDomain() }

    suspend fun vehicleDelete(id: Int): ApiStatus<Boolean> = safeApiAction {
        client.delete(VehiclesApi.vehicleAction(id)).body<ApiResponse<VehicleDto?>>()
    }

    suspend fun vehiclesList(): ApiStatus<List<Vehicle>> = safeApiCall<List<VehicleDto>> {
        client.get(VehiclesApi.VEHICLES).body()
    }.map { dtos -> dtos.map { it.toDomain() } }

    suspend fun vehiclesMakes(): ApiStatus<List<VehicleMaker>> = safeApiCall<List<VehicleMakerDto>> {
        client.get(VehiclesApi.VEHICLE_MAKES).body()
    }.map { dtos -> dtos.map { it.toDomain() } }

    suspend fun vehiclesModels(makeId: Int): ApiStatus<List<VehicleModel>> =
        safeApiCall<List<VehicleModelDto>> {
            client.get(VehiclesApi.vehicleModels(makeId)).body()
        }.map { dtos -> dtos.map { it.toDomain() } }
}
