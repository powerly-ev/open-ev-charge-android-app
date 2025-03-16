package com.powerly.vehicles.vehicleList

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.powerly.core.model.powerly.Vehicle
import com.powerly.resources.R
import com.powerly.ui.dialogs.ItemOptionsMenu
import com.powerly.ui.dialogs.MyDropdownMenu

@Composable
internal fun OptionsMenu(
    vehicle: Vehicle,
    showMenu: () -> Boolean,
    onClose: () -> Unit,
    uiEvents: (VehicleEvents) -> Unit
) {
    MyDropdownMenu(
        onDismiss = onClose,
        expanded = showMenu,
        spacing = 16.dp
    ) {

        ItemOptionsMenu(
            title = stringResource(id = R.string.edit),
            onClick = {
                uiEvents(VehicleEvents.Edit(vehicle))
                onClose()
            }
        )

        ItemOptionsMenu(
            title = stringResource(id = R.string.delete),
            onClick = {
                uiEvents(VehicleEvents.Delete(vehicle))
                onClose()
            }
        )
    }
}

