package com.powerly.charging

import androidx.annotation.DrawableRes
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.powerly.core.model.powerly.ChargingQuantity
import com.powerly.core.model.powerly.Connector
import com.powerly.core.model.powerly.PowerSource
import com.powerly.resources.R
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.containers.MyRow
import com.powerly.ui.dialogs.MyDialog
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
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
        connectors: List<Connector>
    ) -> Unit,
    onDismiss: () -> Unit = {}
) {
    val ps = powerSource() ?: return
    var selectedTime by remember { mutableStateOf(ChargingQuantity()) }
    var connectors = remember { mutableStateListOf<Connector>() }

    MyDialog(
        header = {
            ScreenHeader(
                title = stringResource(id = R.string.station_charging_activate),
                layoutDirection = LayoutDirection.Ltr,
                onClose = onDismiss
            )
        },
        spacing = 16.dp,
        onDismiss = onDismiss
    ) {

        SectionDescription(ps.isNear)

        //charging price
        SectionPrice(
            powerSource = ps,
            quantity = { selectedTime }
        )

        //time slots
        ListTimes(
            times = ps.chargingTimes(),
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
            onClick = {
                onStartCharging(
                    ps.id,
                    selectedTime,
                    connectors
                )
            }
        )

    }
}

@Composable
private fun SectionDescription(isNear: Boolean) {
    if (isNear) Text(
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
    val price = powerSource.price ?: return
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

@Composable
fun StationIcon(
    @DrawableRes icon: Int? = null,
    label: String? = null,
    iconSize: Dp = 32.dp,
    description: String = ""
) {
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MyColors.viewColor
        )
    ) {
        Box(
            Modifier.size(42.dp)
        ) {
            icon?.let {
                Icon(
                    modifier = Modifier
                        .size(iconSize)
                        .align(Alignment.Center),
                    painter = painterResource(id = icon),
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = description,
                )
            }
            label?.let {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = label,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}