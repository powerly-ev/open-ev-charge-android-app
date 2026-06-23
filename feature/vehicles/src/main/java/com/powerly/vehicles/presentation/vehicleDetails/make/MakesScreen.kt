package com.powerly.vehicles.presentation.vehicleDetails.make

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.powerly.core.domain.model.powerly.VehicleMaker
import com.powerly.navigation.IRoute
import org.koin.androidx.compose.koinViewModel

private const val TAG = "MakesScreen"

/**
 * Composable function that displays a screen for selecting a vehicle manufacturer (make).
 *
 * This screen presents a list of vehicle makes fetched from the `viewModel`.
 * Users can select a make, which triggers the `onNext` lambda function with the selected [VehicleMaker].
 * The `onClose` lambda function is called when the user closes the screen.
 *
 * @param direction An [IRoute] object representing the navigation direction and potentially containing page information.
 * @param onNext A lambda function that is called when the user selects a vehicle make. It receives the selected [VehicleMaker] as an argument.
 * @param onClose A lambda function that is called when the user closes the screen, typically navigating back.
 */
@Composable
internal fun MakesScreen(
    viewModel: MakeViewModel = koinViewModel(),
    direction: IRoute,
    onNext: (VehicleMaker) -> Unit,
    onClose: () -> Unit
) {
    val makers = viewModel.getMakes.collectAsStateWithLifecycle()

    MakesScreenContent(
        index = direction.page,
        makers = { makers.value },
        onClose = onClose,
        onNext = onNext
    )
}
