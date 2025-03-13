package com.powerly.vehicles.vehicleDetails.model

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.powerly.core.model.powerly.VehicleModel
import com.powerly.lib.IRoute
import com.powerly.vehicles.VehiclesViewModel

private const val TAG = "ModelScreen"

/**
 * Composable function that displays a screen for selecting a vehicle model.
 *
 * This screen presents a list of vehicle models fetched from the `viewModel`, likely filtered by the previously selected make.
 * Users can select a model, which triggers the `onNext` lambda function with the selected [VehicleModel].
 * The `onClose` lambda function is called when the user closes the screen, typically navigating back.
 *
 * @param viewModel The [VehiclesViewModel] that provides the list of vehicle models.
 * @param direction An [IRoute] object representing the navigation direction and potentially containing page information.
 * @param onClose A lambda function that is called when the user closes the screen, typically navigating back.
 * @param onNext A lambda function that is called when the user selects a vehicle model. It receives the selected [VehicleModel] as an argument.
 */
@Composable
internal fun ModelScreen(
    viewModel: VehiclesViewModel,
    direction: IRoute,
    onClose: () -> Unit,
    onNext: (VehicleModel) -> Unit
) {
    val models = viewModel.getModels.collectAsStateWithLifecycle()
    ModelScreenContent(
        index = direction.page,
        models = { models.value },
        onClose = onClose,
        onNext = onNext
    )
}
