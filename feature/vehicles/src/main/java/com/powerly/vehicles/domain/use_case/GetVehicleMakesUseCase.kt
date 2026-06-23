package com.powerly.vehicles.domain.use_case

import com.powerly.core.domain.model.ApiStatus
import com.powerly.vehicles.domain.model.GroupedMakers
import com.powerly.vehicles.domain.repository.VehiclesRepository
import org.koin.core.annotation.Single

/** Fetches vehicle manufacturers, sorted and grouped by the first letter of their name. */
@Single
class GetVehicleMakesUseCase(
    private val repository: VehiclesRepository
) {
    suspend operator fun invoke(): ApiStatus<GroupedMakers> {
        return when (val result = repository.vehiclesMakes()) {
            is ApiStatus.Success -> ApiStatus.Success(
                result.data
                    .sortedBy { it.name.lowercase() }
                    .groupBy { it.name.first().uppercaseChar().toString() }
            )
            is ApiStatus.Error -> result
            is ApiStatus.Loading -> result
        }
    }
}
