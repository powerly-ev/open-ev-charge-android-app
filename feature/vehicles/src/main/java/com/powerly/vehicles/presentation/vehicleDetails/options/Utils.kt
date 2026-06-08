package com.powerly.vehicles.presentation.vehicleDetails.options

import androidx.annotation.StringRes
import com.powerly.resources.R
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn


internal val fuelTypes = listOf(
    FuelType(
        id = "Gas",
        title = R.string.fuel_fuel
    ), FuelType(
        id = "Electric",
        title = R.string.fuel_electric
    ),
    FuelType(
        id = "Hybrid",
        title = R.string.fuel_hybrid
    ),
    FuelType(
        id = "Hybrid",
        title = R.string.fuel_plug_in_hybrid
    ),
    FuelType(
        id = "Diesel",
        title = R.string.fuel_other
    )
)

internal val currentYear: Int =
    Clock.System.todayIn(TimeZone.currentSystemDefault()).year

val years = (2000..currentYear).map { it.toString() }

internal data class FuelType(
    val id: String,
    @StringRes val title: Int
)