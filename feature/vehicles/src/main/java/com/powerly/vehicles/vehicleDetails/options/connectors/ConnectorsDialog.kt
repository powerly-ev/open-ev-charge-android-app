package com.powerly.vehicles.vehicleDetails.options.connectors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.koin.androidx.compose.koinViewModel
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.Connector
import com.powerly.ui.dialogs.MyDialogState
import com.powerly.ui.dialogs.MyScreenBottomSheet

private const val TAG = "ConnectorsDialog"


@Composable
fun ConnectorsDialog(
    state: MyDialogState,
    selectedConnectors: () -> List<Connector>,
    viewModel: ConnectorsViewModel = koinViewModel(),
    onSelect: (Connector) -> Unit
) {
    MyScreenBottomSheet(state = state) {
        val connectors = viewModel.connectors.collectAsState(initial = ApiStatus.Loading)
        ConnectorsScreenContent(
            connectors = connectors.value,
            selectedIds = selectedConnectors().map { it.id },
            onClose = { state.dismiss() },
            onSelect = {
                onSelect(it)
                state.dismiss()
            }
        )
    }
}
