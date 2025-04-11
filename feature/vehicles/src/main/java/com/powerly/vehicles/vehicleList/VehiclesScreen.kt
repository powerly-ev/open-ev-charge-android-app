package com.powerly.vehicles.vehicleList

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.Vehicle
import com.powerly.vehicles.VehiclesViewModel
import com.powerly.resources.R
import com.powerly.ui.dialogs.alert.MyAlertDialog
import com.powerly.ui.dialogs.alert.rememberAlertDialogState
import com.powerly.ui.dialogs.loading.rememberScreenState
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

private const val TAG = "VehiclesScreen"

/**
 * Displays the Vehicles screen.
 *
 * This composable function is responsible for displaying the list of vehicles,
 * handling user interactions, and triggering navigation to other screens.
 */
@Composable
internal fun VehiclesScreen(
    viewModel: VehiclesViewModel,
    addNewVehicle: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedVehicle by remember { mutableStateOf<Vehicle?>(null) }
    val screenState = rememberScreenState()
    val deleteDialog = rememberAlertDialogState()
    val vehicles = viewModel.vehiclesList.collectAsState(initial = ApiStatus.Loading)

    /**
     * Load vehicles list while screen in launched
     */
    LaunchedEffect(Unit) {
        viewModel.getVehicles()
    }

    val flowCollector = FlowCollector<ApiStatus<Boolean>> {
        screenState.loading = it is ApiStatus.Loading
        when (it) {
            is ApiStatus.Error -> {
                screenState.showMessage(it.msg)
            }

            is ApiStatus.Success -> {
                viewModel.getVehicles()
                screenState.showSuccess()
            }

            else -> {}
        }
    }

    /**
     * Updates an existing vehicle in the data source.
     *
     * This function launches a coroutine to update the given `vehicle` using the
     * `viewModel`. It collects the result of the update operation using the
     * `flowCollector` to handle success and error states.
     *
     * @param vehicle The [Vehicle] object to be updated.
     */
    fun updateVehicle(vehicle: Vehicle) {
        coroutineScope.launch {
            viewModel.updateVehicle(vehicle).collect(flowCollector)
        }
    }

    /**
     * Deletes a vehicle from the data source.
     *
     * This function launches a coroutine to delete the vehicle with the given `id`
     * using the `viewModel`. It collects the result of the delete operation using
     * the `flowCollector` to handle success and error states.
     *
     */
    fun deleteVehicle() {
        val id = selectedVehicle?.id ?: return
        coroutineScope.launch {
            viewModel.deleteVehicle(id).collect(flowCollector)
        }
    }

    MyAlertDialog(
        state = deleteDialog,
        title = stringResource(R.string.vehicle_delete),
        message = stringResource(R.string.vehicle_delete_message),
        positiveButtonClick = ::deleteVehicle,
        negativeButtonClick = {},
    )

    VehiclesScreenContent(
        vehicles = vehicles.value,
        screenState = screenState,
        uiEvents = {
            when (it) {
                is VehicleEvents.Close -> onBack()

                is VehicleEvents.EditTitle -> {
                    updateVehicle(it.vehicle)
                }

                is VehicleEvents.Edit -> {
                    viewModel.setVehicle(it.vehicle)
                    addNewVehicle()
                }

                is VehicleEvents.Delete -> {
                    selectedVehicle = it.vehicle
                    deleteDialog.show()
                }

                is VehicleEvents.Add -> {
                    viewModel.openModels = true
                    viewModel.setVehicle(Vehicle())
                    addNewVehicle()
                }
            }
        }
    )
}
