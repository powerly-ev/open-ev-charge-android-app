package com.powerly.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.powerly.core.model.powerly.Connector
import com.powerly.resources.R
import com.powerly.ui.containers.MyRow
import com.powerly.ui.containers.MySurface
import com.powerly.ui.containers.MySurfaceColumn
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
import kotlin.let

@OptIn(ExperimentalLayoutApi::class)
@Preview
@Composable
fun ItemConnectorPreview() {
    AppTheme() {
        FlowRow(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MySurface(color = Color.White) {
                val connector = Connector(
                    id = 1,
                    name = "Connector 1",
                    icon = "https://example.com/icon.png",
                    maxPower = 150.0,
                    status = "Available"
                )
                ItemConnector(connector = connector)
            }
            MySurface(color = Color.White) {
                val connector = Connector(
                    id = 1,
                    name = "Connector 1",
                    icon = "https://example.com/icon.png",
                    maxPower = 150.0,
                    status = "Unavailable"
                )
                ItemConnector(connector = connector)
            }
            Spacer(Modifier.height(16.dp))
            MySurface(color = Color.White) {
                val connector = Connector(
                    id = 1,
                    name = "Connector 1",
                    icon = "https://example.com/icon.png",
                    maxPower = 150.0,
                    status = "Booked"

                )
                ItemConnector(connector = connector)
            }
            MySurface(color = Color.White) {
                val connector = Connector(
                    id = 1,
                    name = "Connector 1",
                    icon = "https://example.com/icon.png",
                    maxPower = 150.0,
                    status = "Booked by you"
                )
                ItemConnector(connector = connector)
            }
            Spacer(Modifier.height(16.dp))
            MySurface(color = Color.White) {
                val connector = Connector(
                    id = 1,
                    name = "Connector 1",
                    icon = "https://example.com/icon.png",
                    maxPower = 150.0,
                    status = "Busy"

                )
                ItemConnector(connector = connector)
            }
            Spacer(Modifier.height(16.dp))
            MySurface(color = Color.White) {
                val connector = Connector(
                    id = 1,
                    name = "Connector 1",
                    icon = "https://example.com/icon.png",
                    maxPower = 150.0,
                    status = "Busy By You"

                )
                ItemConnector(connector = connector)
            }
        }
    }
}

@Composable
fun ItemConnector(
    connector: Connector,
    width: Dp = 150.dp,
    enabled: () -> Boolean = { false },
    isSelected: () -> Boolean = { false },
    onClick: (() -> Unit)? = null
) {
    MySurfaceColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(width)
            .wrapContentHeight()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        color = when {
            isSelected() -> MyColors.blueLight2.copy(alpha = 0.5f)
            else -> Color.Transparent
        },
        cornerRadius = 8.dp,
        onClick = if (enabled()) onClick else null
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = connector.name,
            maxLines = 1,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        NetworkImage(
            src = connector.icon,
            placeHolder = R.drawable.logo,
            description = connector.name,
            modifier = Modifier.size(40.dp)
        )
        MyRow {
            Icon(
                painter = painterResource(id = R.drawable.charge),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
            MyTextDynamic(
                text = stringResource(R.string.station_max_power, connector.maxPower),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        MySurface(
            color = when {
                connector.isAvailable -> MaterialTheme.colorScheme.surface
                connector.busy -> MaterialTheme.colorScheme.surface
                connector.busyByYou -> MaterialTheme.colorScheme.error
                connector.booked -> MaterialTheme.colorScheme.error
                connector.bookedByYou -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.surface
            },
            cornerRadius = 8.dp,
            modifier = Modifier.padding(8.dp)
        ) {
            MyTextDynamic(
                text = connector.status,
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    connector.isAvailable -> MaterialTheme.colorScheme.primary
                    connector.busy -> MaterialTheme.colorScheme.error
                    connector.busyByYou -> MaterialTheme.colorScheme.secondary
                    connector.booked -> MaterialTheme.colorScheme.secondary
                    connector.bookedByYou -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.secondary
                }
            )
        }
        if (isSelected()) Text(
            text = stringResource(
                R.string.station_details_plug,
                connector.number
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
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