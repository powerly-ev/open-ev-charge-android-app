package com.powerly.vehicles.presentation.vehicleDetails.options

sealed interface OptionsEvents {
    data class Next(val isElectric: Boolean) : OptionsEvents
    data object Close : OptionsEvents
    data object PickColor : OptionsEvents
    data object PickConnector : OptionsEvents
}