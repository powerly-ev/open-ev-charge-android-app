package com.powerly.vehicles.presentation.vehicleList

import com.powerly.core.domain.model.powerly.Vehicle

internal sealed class VehicleEvents {
    data object Close : VehicleEvents()
    data class EditTitle(val vehicle: Vehicle) : VehicleEvents()
    data class Edit(val vehicle: Vehicle) : VehicleEvents()
    data class Delete(val vehicle: Vehicle) : VehicleEvents()
    data object Add : VehicleEvents()
}
