package com.powerly.vehicles.domain.use_case

import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.VehicleModel
import com.powerly.vehicles.domain.repository.VehiclesRepository
import org.koin.core.annotation.Single

/** Fetches vehicle models for a given manufacturer, sorted by name. */
@Single
class GetVehicleModelsUseCase(
    private val repository: VehiclesRepository
) {
    suspend operator fun invoke(makeId: Int): ApiStatus<List<VehicleModel>> {
        return when (val result = repository.vehiclesModels(makeId)) {
            is ApiStatus.Success -> ApiStatus.Success(result.data.sortedBy { it.name })
            is ApiStatus.Error -> result
            is ApiStatus.Loading -> result
        }
    }
}
