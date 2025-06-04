package com.powerly.charge

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.powerly.core.model.powerly.ChargingQuantity
import com.powerly.core.model.powerly.Connector
import com.powerly.core.model.powerly.PowerSource
import com.powerly.core.network.BuildConfig
import com.powerly.resources.R
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.components.ItemConnector
import com.powerly.ui.components.StationIcon
import com.powerly.ui.containers.MyRow
import com.powerly.ui.containers.MySurfaceRow
import com.powerly.ui.dialogs.MyDialog
import com.powerly.ui.screen.DialogHeader
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
import com.powerly.ui.theme.myBorder
import java.util.Locale

private val connectors = listOf(
    Connector(1, "GB/ T", number = 1),
    Connector(2, "GB/ S", number = 2)
)

@Preview
@Composable
private fun ChargingDialogMinutesNearPreview() {
    val ps = PowerSource(
        id = "0", priceUnit = "minutes", distance = 0.1,
        connectors = connectors, sessionLimit = 180, price = 1.0,
        currency = "USD"
    )
    AppTheme {
        ChargingDialog(
            powerSource = { ps },
            onStartCharging = { _, _, _ -> }
        )
    }
}

@Preview
@Composable
private fun ChargingDialogMinutesFrPreview() {
    val ps = PowerSource(
        id = "0", priceUnit = "minutes", distance = 1000.0,
        connectors = connectors, sessionLimit = 180, price = 1.0,
    ).apply { currency = "USD" }
    AppTheme {
        ChargingDialog(
            powerSource = { ps },
            onStartCharging = { _, _, _ -> }
        )
    }
}

@Preview
@Composable
private fun ChargingDialogEnergyPreview() {
    val ps = PowerSource(
        id = "0", priceUnit = "energy", distance = 0.1,
        connectors = connectors, sessionLimit = 180, price = 1.0,
    ).apply { currency = "USD" }
    AppTheme {
        ChargingDialog(
            powerSource = { ps },
            onDismiss = {},
            onStartCharging = { _, _, _ -> }
        )
    }
}


@Composable
fun ChargingDialog(
    powerSource: () -> PowerSource?,
    onStartCharging: (
        chargerPointId: String,
        time: ChargingQuantity,
        connector: Connector?
    ) -> Unit,
    onError: (String) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val ps = powerSource() ?: return
    val context = LocalContext.current
    var selectedTime by remember { mutableStateOf(ChargingQuantity()) }
    var selectedConnector by remember { mutableStateOf<Connector?>(null) }

    fun startCharging() {
        if (ps.hasConnectors && selectedConnector == null) {
            val message = context.getString(R.string.station_charging_start_error)
            onError(message)
        } else {
            onStartCharging(
                ps.id,
                selectedTime,
                selectedConnector
            )
        }
    }

    MyDialog(
        header = {
            DialogHeader(
                title = stringResource(id = R.string.station_charging_activate),
                layoutDirection = LayoutDirection.Ltr,
                onClose = onDismiss
            )
        },
        spacing = 16.dp,
        onDismiss = onDismiss
    ) {

        if (ps.isNear) Text(
            text = stringResource(id = R.string.station_charging_msg_1),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        ) else {
            Text(
                text = stringResource(id = R.string.station_charging_msg_2),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = stringResource(id = R.string.station_charging_msg_3),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = stringResource(id = R.string.station_charging_msg_4),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }


        //charging price
        SectionPrice(
            powerSource = ps,
            quantity = { selectedTime }
        )

        // connectors/plugs list
        if (ps.isEvCharger) SectionConnectors(
            connectors = ps.connectors.orEmpty(),
            selected = { selectedConnector },
            onSelected = { selectedConnector = it }
        )

        //time slots
        ListTimes(
            times = ps.chargingTimes(includeTest = BuildConfig.DEBUG),
            selectedTime = { selectedTime },
            onSelectTime = { selectedTime = it }
        )

        ButtonLarge(
            text = stringResource(id = R.string.station_charging_start),
            color = MyColors.white,
            background = MaterialTheme.colorScheme.secondary,
            icon = R.drawable.charge,
            layoutDirection = LayoutDirection.Rtl,
            modifier = Modifier.fillMaxWidth(),
            onClick = ::startCharging
        )

    }
}

@Composable
private fun SectionConnectors(
    connectors: List<Connector>,
    selected: () -> Connector?,
    onSelected: (Connector) -> Unit
) {
    if (connectors.isEmpty()) return
    MySurfaceRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .horizontalScroll(rememberScrollState()),
        spacing = 16.dp,
        cornerRadius = 8.dp,
        border = myBorder,
        color = Color.Transparent
    ) {
        connectors.forEach {
            ItemConnector(
                connector = it,
                width = 120.dp,
                enabled = { it.isAvailable || it.bookedByYou },
                isSelected = { it.id == selected()?.id },
                onClick = { onSelected(it) }
            )
        }
    }
}

@Composable
fun ListTimes(
    times: List<ChargingQuantity>,
    selectedTime: () -> ChargingQuantity,
    margin: PaddingValues = PaddingValues(0.dp, 0.dp),
    onSelectTime: (time: ChargingQuantity) -> Unit
) {
    MyRow(
        modifier = Modifier
            .padding(margin)
            .fillMaxWidth()
            .height(50.dp)
            .horizontalScroll(rememberScrollState()),
        spacing = 16.dp,
    ) {
        times.forEach { time ->
            ItemTime(
                time = time,
                selected = time.minutes == selectedTime().minutes,
                onClick = {
                    onSelectTime(time)
                }
            )
        }
    }
}


@Composable
private fun ItemTime(
    time: ChargingQuantity,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) MyColors.blueLight
        else MyColors.white,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.height(IntrinsicSize.Min),
        onClick = onClick
    ) {
        MyRow(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
        ) {
            Icon(
                painter = painterResource(
                    id = if (time.isFull) R.drawable.rocket
                    else R.drawable.ic_baseline_access_time_24,
                ),
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = null
            )

            val minute = stringResource(id = R.string.station_charging_minute)
            val hour = stringResource(id = R.string.station_charging_hour)
            Text(
                text = if (time.isFull) stringResource(id = R.string.station_charging_full)
                else time.toTime(minute = minute, hour = hour),
                modifier = Modifier.width(IntrinsicSize.Max),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun SectionPrice(
    powerSource: PowerSource,
    quantity: () -> ChargingQuantity
) {
    val price = powerSource.price
    val q = quantity()
    val minutes = if (q.isFull || powerSource.isTimedPrice.not()) 1 else q.minutes

    MyRow(modifier = Modifier.fillMaxWidth(), spacing = 4.dp) {
        StationIcon(icon = R.drawable.dollar_symbol)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            powerSource.currency,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            "%.2f".format(Locale.US, price * minutes),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
        val chargingUnit = if (powerSource.isTimedPrice) {
            if (minutes == 1) stringResource(R.string.station_per_minute)
            else q.toTime(
                per = stringResource(R.string.station_per),
                hour = stringResource(R.string.station_charging_hour),
                minute = stringResource(R.string.station_charging_minute)
            )
        } else stringResource(R.string.station_per_kwh)

        Text(
            chargingUnit.replaceFirstChar(Char::lowercase),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}