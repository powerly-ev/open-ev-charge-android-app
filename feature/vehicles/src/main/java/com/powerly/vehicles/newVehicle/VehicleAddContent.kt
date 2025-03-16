package com.powerly.vehicles.newVehicle

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.powerly.core.model.powerly.Vehicle
import com.powerly.core.model.powerly.VehicleMaker
import com.powerly.resources.R
import com.powerly.ui.containers.MyCardRow
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.dialogs.loading.ScreenState
import com.powerly.ui.dialogs.loading.rememberScreenState
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors

private const val TAG = "VehicleAddScreen"

@Preview
@Composable
private fun VehicleAddScreenPreview() {
    val vehicle = Vehicle(
        maker = VehicleMaker(1, name = "New")
    )
    AppTheme {
        VehicleAddScreenContent(
            vehicle = vehicle,
            uiEvents = {}
        )
    }
}

@Composable
internal fun VehicleAddScreenContent(
    screenState: ScreenState = rememberScreenState(),
    vehicle: Vehicle,
    uiEvents: (VehicleAddEvents) -> Unit
) {
    MyScreen(
        screenState = screenState,
        header = {
            ScreenHeader(
                closeIcon = R.drawable.arrow_back,
                title = stringResource(id = R.string.vehicle_add),
                onClose = { uiEvents(VehicleAddEvents.Close) }
            )
        }, modifier = Modifier.padding(16.dp)
    ) {

        SectionVehicleDetail(
            title = R.string.vehicle_manufacturer,
            checked = { vehicle.maker != null },
            subTitle = { vehicle.maker?.name },
            clickable = { true },
            onClick = { uiEvents(VehicleAddEvents.Manufacturer) }
        )
        SectionVehicleDetail(
            title = R.string.vehicle_model,
            checked = { vehicle.model != null },
            clickable = { vehicle.maker != null },
            subTitle = { vehicle.model?.name },
            onClick = { uiEvents(VehicleAddEvents.Model) }
        )
        SectionVehicleDetail(
            title = R.string.vehicle_details,
            checked = { vehicle.year != null && vehicle.fuelType != null },
            clickable = { vehicle.model != null },
            subTitle = { vehicle.color },
            onClick = { uiEvents(VehicleAddEvents.Details) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ButtonLarge(
            modifier = Modifier.fillMaxWidth(),
            onClick = { uiEvents(VehicleAddEvents.Add) },
            layoutDirection = LayoutDirection.Rtl,
            text = stringResource(
                id = if (vehicle.id == null) R.string.vehicle_add
                else R.string.update
            ),
            icon = R.drawable.ic_add,
            background = MyColors.grey250,
            disabledBackground = MyColors.disabledColor,
            color = MaterialTheme.colorScheme.secondary,
            enabled = { vehicle.isInitialized }
        )
    }
}


@Composable
private fun SectionVehicleDetail(
    @StringRes title: Int = R.string.app_name,
    subTitle: () -> String? = { null },
    checked: () -> Boolean = { true },
    clickable: () -> Boolean = { true },
    onClick: () -> Unit = {},
) {
    val isChecked by remember { mutableStateOf(checked()) }
    MyCardRow(
        padding = PaddingValues(16.dp),
        spacing = 16.dp,
        onClick = { if (clickable()) onClick() },
        fillMaxWidth = true
    ) {
        Icon(
            painter = painterResource(id = R.drawable.checked_sign),
            contentDescription = "",
            modifier = Modifier.size(18.dp),
            tint = if (isChecked) MaterialTheme.colorScheme.primary
            else MyColors.disabledColor
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "* ${stringResource(id = title)}",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyLarge,
            )
            val value = subTitle()
            if (value.isNullOrEmpty().not()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value!!,
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_down),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}