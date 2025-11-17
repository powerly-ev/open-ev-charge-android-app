package com.powerly.powerSource.details.activate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.powerly.core.model.powerly.PowerSource
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun ActivateChargerDialog(
    powerSource: () -> PowerSource?,
    viewModel: ActivateChargerViewModel = koinViewModel(),
    onOpenChargingScreen: (orderId: String) -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val screenState = remember { viewModel.screenState }
    ActivateChargerDialogContent(
        screenState = screenState,
        powerSource = powerSource,
        onStartCharging = { chargePointId, quantity, connector ->
            coroutineScope.launch {
                val orderId = viewModel.startCharging(chargePointId, quantity, connector)
                if (orderId != null) onOpenChargingScreen(orderId)
            }
        },
        onDismiss = onDismiss
    )
}