package com.powerly.vehicles.vehicleDetails.options

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.powerly.lib.IRoute
import com.powerly.vehicles.VehiclesViewModel
import com.powerly.vehicles.vehicleDetails.options.connectors.ConnectorsDialog
import com.powerly.resources.R
import com.powerly.ui.components.rememberPickerState
import com.powerly.ui.dialogs.rememberMyDialogState

private const val TAG = "OptionsScreen"

/**
 * Composable function that displays a screen for configuring vehicle details.
 *
 * This screen allows the user to select the vehicle's color, year, fuel type, and connector type.
 * It utilizes color and connector picker fragments for user interaction.
 * The selected details are updated in the `viewModel` when the user navigates back.
 *
 * @param viewModel The [VehiclesViewModel] that holds the vehicle data.
 * @param direction An [IRoute] object representing the navigation direction and potentially containing page information.
 * @param onBack A lambda function that is called when the user navigates back from this screen.
 */
@Composable
internal fun DetailsScreen(
    viewModel: VehiclesViewModel,
    direction: IRoute,
    onBack: () -> Unit
) {
    val vehicle by remember { viewModel.vehicle }
    //color
    val colorPickerDialog = rememberMyDialogState()
    val color = rememberMyColorPickerState(default = vehicle.color?.asVehicleColor)
    //connector/plug
    val connectorsDialog = rememberMyDialogState()
    var connector by remember { mutableStateOf(vehicle.connector) }
    // year
    val year = rememberPickerState(default = vehicle.year ?: "$currentYear")
    // fuel type
    val fuelType = rememberPickerState(
        default = vehicle.fuelType ?: stringResource(id = R.string.fuel_electric)
    )


    fun uiEvents(it: OptionsEvents) {
        when (it) {
            OptionsEvents.Close -> {
                onBack()
            }

            is OptionsEvents.Next -> {
                with(vehicle) {
                    this.color = color.selected?.name.orEmpty()
                    this.year = year.value
                    this.fuelType = fuelType.value
                    this.connector = if (it.isElectric) connector else null
                }
                onBack()
            }

            OptionsEvents.PickColor -> {
                colorPickerDialog.show()
            }

            OptionsEvents.PickConnector -> {
                connectorsDialog.show()
            }
        }
    }

    ConnectorsDialog(
        state = connectorsDialog,
        onSelect = { connector = it },
        selectedConnectors = { if (connector != null) listOf(connector!!) else listOf() }
    )

    ColorPickerDialog(
        state = colorPickerDialog,
        selectedColor = { color.selected },
        onSelect = { color.selected = it }
    )

    OptionsScreenContent(
        index = direction.page,
        color = color,
        year = year,
        fuelType = fuelType,
        connector = { connector },
        action = ::uiEvents
    )
}
