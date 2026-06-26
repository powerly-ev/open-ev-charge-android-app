package com.powerly.vehicles.presentation.newVehicle

internal sealed class VehicleAddEvents {
    data object Close : VehicleAddEvents()
    data object Manufacturer : VehicleAddEvents()
    data object Model : VehicleAddEvents()
    data object Details : VehicleAddEvents()
    data object Add : VehicleAddEvents()
}