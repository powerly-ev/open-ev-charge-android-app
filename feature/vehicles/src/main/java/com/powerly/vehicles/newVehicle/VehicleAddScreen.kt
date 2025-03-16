package com.powerly.vehicles.newVehicle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.Vehicle
import com.powerly.vehicles.VehiclesViewModel
import com.powerly.ui.dialogs.loading.rememberScreenState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "VehicleAddScreen"

/**
 * Composable function that displays the UI for adding or editing a vehicle.
 *
 * This screen allows the user to input vehicle details, such as manufacturer, model, and options.
 * It also handles navigation to other screens for selecting manufacturer, model, and options.
 * When the user clicks the "Add" button, it updates the vehicle in the ViewModel and navigates back.
 *
 * @param viewModel The [VehiclesViewModel] that provides data and handles vehicle operations.
 * @param openManufacturer A lambda function that is called when the user clicks the "Manufacturer" button.
 * @param openModel A lambda function that is called when the user clicks the "Model" button.
 * @param openOptions A lambda function that is called when the user clicks the "Options" button.
 * @param onBack A lambda function that is called when the user clicks the "Back" button.
 */
@Composable
internal fun VehicleAddScreen(
    viewModel: VehiclesViewModel,
    openManufacturer: () -> Unit,
    openModel: () -> Unit,
    openOptions: () -> Unit,
    onBack: () -> Unit
) {
    val vehicle by remember { viewModel.vehicle }
    val coroutineScope = rememberCoroutineScope()
    val screenState = rememberScreenState()

    /**
     * Updates the vehicle information.
     *
     * This function launches a coroutine to update the vehicle information using the provided [vehicle] object.
     * It displays a progress dialog while the update is in progress and handles the API response.
     * If the update is successful, it shows a success dialog and triggers the [onBack] lambda function.
     * If there is an error, it displays an error message using a toast.
     *
     * @param vehicle The [Vehicle] object containing the updated information.
     */
    fun updateVehicle(vehicle: Vehicle) {
        coroutineScope.launch {
            viewModel.updateVehicle(vehicle).collect { state ->
                screenState.loading = state is ApiStatus.Loading
                when (state) {
                    is ApiStatus.Error -> {
                        screenState.showMessage(state.msg)
                    }

                    is ApiStatus.Success -> {
                        screenState.showSuccess { onBack() }
                    }

                    else -> {}
                }
            }
        }
    }

    /**
     * Launches an effect that navigates to the manufacturer selection screen.
     *
     * This effect is triggered when `viewModel.openModels` is true and the vehicle is not initialized.
     * It sets `viewModel.openModels` to false, delays for 1 second, and then calls the `openManufacturer` lambda function
     * to navigate to the manufacturer selection screen.
     *
     * This logic is likely used to ensure that the manufacturer selection screen is opened only once
     * when the screen is first loaded and the vehicle is not yet initialized.
     */
    LaunchedEffect(key1 = Unit) {
        if (viewModel.openModels && vehicle.isInitialized.not()) {
            coroutineScope.launch {
                viewModel.openModels = false
                delay(1000)
                openManufacturer()
            }
        }
    }

    VehicleAddScreenContent(
        screenState = screenState,
        vehicle = vehicle,
        uiEvents = {
            when (it) {
                VehicleAddEvents.Close -> {
                    onBack()
                }

                VehicleAddEvents.Manufacturer -> {
                    openManufacturer()
                }

                VehicleAddEvents.Model -> {
                    openModel()
                }

                VehicleAddEvents.Details -> {
                    openOptions()
                }

                VehicleAddEvents.Add -> {
                    updateVehicle(vehicle)
                }
            }
        }
    )
}
