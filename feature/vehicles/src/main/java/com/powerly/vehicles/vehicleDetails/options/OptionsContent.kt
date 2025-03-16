package com.powerly.vehicles.vehicleDetails.options

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.powerly.core.model.powerly.Connector
import com.powerly.resources.R
import com.powerly.ui.containers.MyCardColum
import com.powerly.ui.containers.MyCardRow
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.screen.IndexedScreenHeader
import com.powerly.ui.components.MySlidingPicker
import com.powerly.ui.components.PickerState
import com.powerly.ui.components.rememberPickerState
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors

@Preview
@Composable
private fun OptionsScreenPreview() {
    val fuelType = stringResource(id = R.string.fuel_electric)
    AppTheme {
        OptionsScreenContent(
            index = 1,
            color = rememberMyColorPickerState(),
            year = rememberPickerState("2020"),
            fuelType = rememberPickerState(fuelType),
            connector = { null },
            action = {}
        )
    }
}

@Composable
internal fun OptionsScreenContent(
    index: Int,
    color: MyColorPickerState,
    year: PickerState,
    fuelType: PickerState,
    connector: () -> Connector?,
    action: (OptionsEvents) -> Unit
) {
    val electricTypes = listOf(
        stringResource(id = R.string.fuel_electric),
        stringResource(id = R.string.fuel_plug_in_hybrid)
    )

    fun isElectricType(): Boolean = electricTypes.contains(fuelType.value)

    MyScreen(
        header = {
            IndexedScreenHeader(
                index = index,
                pages = 3,
                title = stringResource(id = R.string.vehicle_details),
                onClose = { action(OptionsEvents.Close) },
            )
        },
        footer = {
            Box(modifier = Modifier.padding(16.dp)) {
                ButtonLarge(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        action(OptionsEvents.Next(isElectricType()))
                    },
                    enabled = {
                        color.selected != null && (!isElectricType()
                                || (isElectricType() && connector() != null))
                    },
                    layoutDirection = LayoutDirection.Ltr,
                    text = stringResource(id = R.string.station_save_next),
                    icon = R.drawable.arrow_right,
                    background = MaterialTheme.colorScheme.secondary,
                    color = MyColors.white,
                )
            }
        },
        verticalScroll = true,
        modifier = Modifier.padding(16.dp)
    ) {
        SectionColor(
            colorState = color,
            showColorPicker = { action(OptionsEvents.PickColor) }
        )
        SectionYear(state = year)
        SectionFuelType(state = fuelType)
        SectionChargingType(
            plug = connector,
            visible = { isElectricType() },
            onClick = { action(OptionsEvents.PickConnector) }
        )
    }
}

@Composable
private fun SectionChargingType(
    plug: () -> Connector?,
    visible: () -> Boolean,
    onClick: () -> Unit
) {

    AnimatedVisibility(
        visible = visible(),
        enter = fadeIn(),
        exit = fadeOut(),
    ) {

        MyCardColum(
            padding = PaddingValues(
                horizontal = 8.dp,
                vertical = 16.dp
            ),
            spacing = 8.dp,
            onClick = onClick,
        ) {
            Text(
                text = "* " + stringResource(
                    id = R.string.vehicle_details_charging,
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            MyCardRow(
                padding = PaddingValues(
                    horizontal = 8.dp,
                    vertical = 16.dp
                ),
                spacing = 8.dp,
                background = MyColors.viewColor
            ) {
                val select = stringResource(id = R.string.select)
                Text(
                    text = plug()?.name ?: select,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(id = R.drawable.arrow_right),
                    contentDescription = ""
                )
            }
        }
    }
}

@Composable
private fun SectionFuelType(
    state: PickerState
) {
    MyCardColum(
        padding = PaddingValues(
            horizontal = 8.dp,
            vertical = 16.dp
        ),
        spacing = 8.dp
    ) {
        Text(
            text = "* " + stringResource(
                id = R.string.vehicle_details_fuel,
                state.value
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        MySlidingPicker(
            items = fuelTypes.map { stringResource(id = it.title) },
            pickerState = state,
            fontSize = 20.sp
        )
    }
}

@Composable
private fun SectionYear(
    state: PickerState
) {
    MyCardColum(
        padding = PaddingValues(
            horizontal = 8.dp,
            vertical = 16.dp
        ),
        spacing = 8.dp
    ) {
        Text(
            text = "* " + stringResource(
                id = R.string.vehicle_details_year,
                state.value
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        MySlidingPicker(
            items = years,
            pickerState = state,
            fontSize = 20.sp
        )
    }
}


@Composable
private fun SectionColor(
    colorState: MyColorPickerState,
    showColorPicker: () -> Unit
) {
    val color = colorState.selected

    MyCardColum(
        padding = PaddingValues(
            horizontal = 8.dp,
            vertical = 16.dp
        ),
        spacing = 8.dp
    ) {
        Text(
            text = "* " + stringResource(
                id = R.string.vehicle_details_color,
            ) + if (color != null) " (${color.name})" else "",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        MyCardRow(
            padding = PaddingValues(
                horizontal = 16.dp,
                vertical = 4.dp
            ),
            spacing = 8.dp,
            background = MyColors.viewColor
        ) {

            color?.let { color ->
                if (!mainColors.any { it.id == color.id })
                    mainColors[0] = color
            }

            mainColors.forEach {
                ItemColor(
                    selected = it.id == color?.id,
                    color = it.color,
                    onClick = { colorState.selected = it }
                )
            }
            Image(
                painter = painterResource(id = R.drawable.color_picker),
                contentDescription = "",
                modifier = Modifier
                    .size(32.dp)
                    .clickable(onClick = showColorPicker)
            )
        }
    }
}

