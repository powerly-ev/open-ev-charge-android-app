package com.powerly.vehicles.presentation.vehicleDetails.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.model.powerly.VehicleModel
import com.powerly.navigation.IRoute
import org.koin.androidx.compose.koinViewModel

private const val TAG = "ModelScreen"

/**
 * Composable function that displays a screen for selecting a vehicle model.
 *
 * This screen presents a list of vehicle models fetched from the `viewModel`, likely filtered by the previously selected make.
 * Users can select a model, which triggers the `onNext` lambda function with the selected [VehicleModel].
 * The `onClose` lambda function is called when the user closes the screen, typically navigating back.
 *
 * @param direction An [IRoute] object representing the navigation direction and potentially containing page information.
 * @param onClose A lambda function that is called when the user closes the screen, typically navigating back.
 * @param onNext A lambda function that is called when the user selects a vehicle model. It receives the selected [VehicleModel] as an argument.
 */
@Composable
internal fun ModelScreen(
    viewModel: ModelViewModel = koinViewModel(),
    makeId: Int,
    direction: IRoute,
    onClose: () -> Unit,
    onNext: (VehicleModel) -> Unit
) {
    val models = remember(makeId) { viewModel.getModels(makeId) }
        .collectAsStateWithLifecycle(initialValue = ApiStatus.Loading)
    ModelScreenContent(
        index = direction.page,
        models = { models.value },
        onClose = onClose,
        onNext = onNext
    )
}
