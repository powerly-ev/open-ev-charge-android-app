package com.powerly.vehicles.vehicleList

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.Connector
import com.powerly.core.model.powerly.Vehicle
import com.powerly.core.model.powerly.VehicleMaker
import com.powerly.core.model.powerly.VehicleModel
import com.powerly.vehicles.vehicleDetails.options.asVehicleColor
import com.powerly.resources.R
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.containers.MyRow
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.components.FourCellsContainer
import com.powerly.ui.components.MyTextDynamic
import com.powerly.ui.dialogs.MyProgressView
import com.powerly.ui.dialogs.inputDialog.MyInputDialog
import com.powerly.ui.dialogs.loading.ScreenState
import com.powerly.ui.dialogs.loading.rememberScreenState
import com.powerly.ui.dialogs.rememberMyDialogState
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors

@Preview
@Composable
private fun VehiclesPreview() {

    val vehicles = listOf(
        Vehicle(
            id = 1,
            title = "Vehicle 1 Hybridsdddddddd dddddddd dddddddddd",
            version = "Plug-in 1",
            model = VehicleModel(name = "A320"),
            maker = VehicleMaker(name = "Audi"),
            connector = Connector(name = "Mennekes")
        ),
        Vehicle(
            id = 2,
            title = "Vehicle 2 ",
            version = "Plug-in Hybrid",
            model = VehicleModel(name = "Z540"),
            maker = VehicleMaker(name = "Mitsubishi"),
            connector = Connector(name = "Mennekes")
        )
    )

    val state = ApiStatus.Success(vehicles)

    AppTheme {
        VehiclesScreenContent(
            vehicles = state,
            uiEvents = {}
        )
    }
}

@Composable
internal fun VehiclesScreenContent(
    screenState: ScreenState = rememberScreenState(),
    vehicles: ApiStatus<List<Vehicle>>,
    uiEvents: (VehicleEvents) -> Unit
) {
    MyScreen(
        header = {
            ScreenHeader(
                title = stringResource(id = R.string.vehicle_title),
                onClose = { uiEvents(VehicleEvents.Close) }
            )
        },
        modifier = Modifier.padding(16.dp),
        screenState = screenState,
        spacing = 16.dp
    ) {
        MyColumn(
            modifier = Modifier
                .weight(1f)
                .height(IntrinsicSize.Max)
                .verticalScroll(rememberScrollState()),
            spacing = 8.dp
        ) {
            when (vehicles) {
                is ApiStatus.Loading -> MyProgressView()

                is ApiStatus.Error -> {}

                is ApiStatus.Success -> {
                    vehicles.data.forEach {
                        ItemVehicle(it, uiEvents)
                    }
                }
            }
        }
        ButtonLarge(
            modifier = Modifier.fillMaxWidth(),
            onClick = { uiEvents(VehicleEvents.Add) },
            layoutDirection = LayoutDirection.Rtl,
            text = stringResource(id = R.string.add),
            icon = R.drawable.ic_add,
            background = MaterialTheme.colorScheme.secondary,
            color = MyColors.white
        )

    }
}

@Composable
private fun ItemVehicle(
    vehicle: Vehicle,
    uiEvents: (VehicleEvents) -> Unit
) {

    Column {

        SectionTitle(vehicle, uiEvents)

        Spacer(modifier = Modifier.height(8.dp))

        FourCellsContainer(
            containerHeight = 200.dp,
            cell1 = {
                Cell(
                    title = R.string.vehicle_make,
                    value = vehicle.maker?.name.orEmpty(),
                    icon = R.drawable.car
                )
            },
            cell2 = {
                Cell(
                    title = R.string.vehicle_model,
                    value = vehicle.model?.name.orEmpty(),
                    icon = R.drawable.car2,
                    tint = vehicle.color?.asVehicleColor?.color
                        ?: MaterialTheme.colorScheme.secondary
                )
            },
            cell3 = {
                Cell(
                    title = R.string.station_plug_type,
                    value = vehicle.connector?.name.orEmpty(),
                    icon = R.drawable.evconnector
                )
            },
            cell4 = {
                Cell(
                    title = R.string.vehicle_version,
                    value = vehicle.fuelType.orEmpty(),
                    icon = R.drawable.update
                )
            }
        )
    }
}

@Composable
private fun Cell(
    @StringRes title: Int,
    value: String,
    @DrawableRes icon: Int,
    tint: Color = MaterialTheme.colorScheme.secondary,
) {
    val iconSize = 32.dp
    MyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Spacer(modifier = Modifier.weight(0.05f))
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "",
            modifier = Modifier.size(iconSize),
            tint = tint
        )
        Column(Modifier.weight(0.4f)) {
            Text(
                text = stringResource(id = title),
                color = MyColors.subColor,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = value,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 2,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun SectionTitle(
    vehicle: Vehicle,
    uiEvents: (VehicleEvents) -> Unit
) {

    val titleDialog = rememberMyDialogState()
    MyInputDialog(
        state = titleDialog,
        default = vehicle.title.orEmpty(),
        title = R.string.station_details_title,
        hint = R.string.station_details_title,
        buttonTitle = R.string.update,
        headerAlign = TextAlign.Start,
        onUpdate = { newTitle ->
            val newVehicle = vehicle.apply {
                this.title = newTitle
            }
            uiEvents(VehicleEvents.EditTitle(newVehicle))
        }
    )

    Box {

        var showMenu by remember { mutableStateOf(false) }
        OptionsMenu(
            vehicle = vehicle,
            showMenu = { showMenu },
            onClose = { showMenu = false },
            uiEvents = uiEvents
        )

        MyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MyRow(
                modifier = Modifier
                    .weight(1f)
                    .clickable { titleDialog.show() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                MyTextDynamic(
                    text = vehicle.title.orEmpty().take(25),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.menu_settings),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.clickable { showMenu = true }
            )
        }
    }
}

