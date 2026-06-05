package com.powerly.vehicles.data.datasource.remote

import com.powerly.core.network.api.ApiResponse
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.model.powerly.Vehicle
import com.powerly.core.model.powerly.VehicleMaker
import com.powerly.core.model.powerly.VehicleModel
import com.powerly.core.network.KtorClient
import com.powerly.core.network.safeApiAction
import com.powerly.core.network.safeApiCall
import com.powerly.vehicles.data.api.VehiclesApi
import com.powerly.vehicles.data.model.VehicleAddBody
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

    suspend fun vehicleAdd(request: VehicleAddBody): ApiStatus<Vehicle> = safeApiCall {
        client.post(VehiclesApi.VEHICLES) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun vehicleUpdate(id: Int, request: VehicleAddBody): ApiStatus<Vehicle> = safeApiCall {
        client.put(VehiclesApi.vehicleAction(id)) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun vehicleDelete(id: Int): ApiStatus<Boolean> = safeApiAction {
        client.delete(VehiclesApi.vehicleAction(id)).body<ApiResponse<Vehicle?>>()
    }

    suspend fun vehiclesList(): ApiStatus<List<Vehicle>> = safeApiCall {
        client.get(VehiclesApi.VEHICLES).body()
    }

    suspend fun vehiclesMakes(): ApiStatus<List<VehicleMaker>> = safeApiCall {
        client.get(VehiclesApi.VEHICLE_MAKES).body()
    }

    suspend fun vehiclesModels(makeId: Int): ApiStatus<List<VehicleModel>> = safeApiCall {
        client.get(VehiclesApi.vehicleModels(makeId)).body()
    }
}
