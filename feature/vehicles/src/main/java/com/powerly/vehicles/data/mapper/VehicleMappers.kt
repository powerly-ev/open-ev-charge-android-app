package com.powerly.vehicles.data.mapper

import com.powerly.core.model.powerly.Vehicle
import com.powerly.vehicles.data.model.VehicleAddBody

internal fun Vehicle.toAddBody(): VehicleAddBody = VehicleAddBody(
    title = title ?: maker?.name,
    vehicleMakeId = maker?.id,
    vehicleMakeName = maker?.name,
    vehicleModelId = model?.id,
    active = active,
    year = year,
    version = version,
    color = color,
    licensePlate = licensePlate,
    fuelType = fuelType,
    chargingConnectorId = connector?.id,
)
