package com.powerly.powerSource.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.powerly.core.model.location.MyAddress
import com.powerly.core.model.powerly.Amenity
import com.powerly.core.model.powerly.Connector
import com.powerly.core.model.powerly.Media
import com.powerly.core.model.powerly.PowerSource
import com.powerly.resources.R
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.components.ButtonSmall
import com.powerly.ui.components.ItemConnector
import com.powerly.ui.components.MyTextDynamic
import com.powerly.ui.components.NetworkImage
import com.powerly.ui.components.RatingBar
import com.powerly.ui.components.StationIcon
import com.powerly.ui.containers.LayoutDirectionLtr
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.containers.MyRow
import com.powerly.ui.containers.MySurfaceColumn
import com.powerly.ui.containers.MySurfaceRow
import com.powerly.ui.dialogs.loading.ScreenState
import com.powerly.ui.dialogs.loading.rememberScreenState
import com.powerly.ui.extensions.asBorder
import com.powerly.ui.extensions.onClick
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
import java.util.Locale

private const val TAG = "StationScreen"

private val powerSource = PowerSource(
    id = "1",
    title = "station 1",
    _openTime = "00:00:00",
    _closeTime = "23:59:00",
    onlineStatus = 1,
    priceUnit = PowerSource.UNIT_TIME,
    distance = 0.9,
    sessionLimit = 180,
    contactNumber = null,
    connectors = listOf(
        Connector(
            name = "Type 1",
            maxPower = 120.0,
            status = "available"
        )
    ),
    address = MyAddress("Egypt"),
    media = listOf(
        Media(1, title = "media 1"),
        Media(2, title = "media 2"),
        Media(3, title = "media 3"),
        Media(4, title = "media 4")
    ),
    price = 3.0,
    rating = 3.53,
).apply {
    currency = "USD"
}

@Preview
@Composable
private fun PowerSourceScreenPreview() {
    AppTheme {
        PowerSourceContent(
            powerSource = { powerSource },
            balance = "120.5",
            uiEvents = {}
        )
    }
}

@Composable
internal fun PowerSourceContent(
    screenState: ScreenState = rememberScreenState(),
    powerSource: () -> PowerSource?,
    balance: String,
    uiEvents: (SourceEvents) -> Unit,
) {
    val ps = powerSource()
    MyScreen(
        screenState = screenState,
        header = {
            ps?.let { powerSource ->
                Header(
                    title = powerSource.title,
                    isVerified = true,
                    onClose = { uiEvents(SourceEvents.Close) },
                    hasContact = powerSource.contactNumber.isNullOrEmpty().not(),
                    onContact = { uiEvents(SourceEvents.CAll(powerSource.contactNumber)) }
                )
            }
        },
        spacing = 16.dp,
        modifier = Modifier.padding(16.dp),
        verticalScroll = true
    ) {
        if (ps == null) return@MyScreen

        SectionStation(
            powerSource = ps,
            onReviews = { uiEvents(SourceEvents.Reviews) },
            onMediaClick = { uiEvents(SourceEvents.Media) }
        )

        SectionCharging(
            powerSource = ps,
            balance = balance,
            uiEvents = uiEvents,
            onCharge = { uiEvents(SourceEvents.Charge) }
        )

        SectionLocation(
            powerSource = ps,
            showMaxPower = true,
            onDriveToStation = {
                uiEvents(SourceEvents.DriveToStation(ps.location))
            }
        )
        if (ps.hasConnectors) SectionConnectors(ps.connectors.orEmpty())
        SectionDetails(powerSource = ps)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Header(
    title: String,
    hasContact: Boolean,
    onClose: () -> Unit,
    onContact: () -> Unit,
    isVerified: Boolean = true
) {
    LayoutDirectionLtr {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(0.dp, 0.dp, 8.dp, 8.dp)),
            title = {
                MyRow(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isVerified) {
                            Icon(
                                painter = painterResource(id = R.drawable.checked_sign),
                                modifier = Modifier.size(18.dp),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        MyTextDynamic(
                            text = title,
                            modifier = Modifier.wrapContentWidth(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                textAlign = TextAlign.Center
                            )
                        )

                    }

                    if (hasContact) IconButton(onClick = onContact) {
                        Icon(
                            painter = painterResource(id = R.drawable.call_icon),
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(24.dp),
                            contentDescription = null
                        )
                    } else Spacer(modifier = Modifier.size(40.dp))
                }
            },
            navigationIcon = {
                IconButton(onClick = onClose) {
                    Icon(
                        painter = painterResource(id = R.drawable.close),
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = null
                    )
                }
            }
        )
    }
}

@Composable
private fun SectionStation(
    powerSource: PowerSource,
    onReviews: () -> Unit,
    onMediaClick: () -> Unit
) {
    MySurfaceColumn(
        modifier = Modifier.padding(16.dp),
        color = Color.White,
        border = 1.dp.asBorder,
        spacing = 8.dp
    ) {
        val context = LocalContext.current
        val sourceUptime = remember { PowerSourceUptime(powerSource, context) }

        //Open time
        if (sourceUptime.hasTime) MyRow(
            spacing = 8.dp,
            verticalAlignment = Alignment.Top
        ) {
            StationIcon(icon = R.drawable.ic_baseline_access_time_24)
            MyColumn(
                modifier = Modifier.weight(1f),
                spacing = 4.dp
            ) {
                Text(
                    text = sourceUptime.workStatus,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (powerSource.isAvailable) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary
                )
                MyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    spacing = 4.dp
                ) {
                    sourceUptime.workTime?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    sourceUptime.icon?.let { icon ->
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = "",
                            modifier = Modifier.size(18.dp),
                            tint = MyColors.red500
                        )
                        Text(
                            text = sourceUptime.status,
                            style = MaterialTheme.typography.bodySmall,
                            color = MyColors.red500,
                        )
                    }
                }
            }
            Icon(
                painter = painterResource(
                    id = R.drawable.baseline_favorite_border_24
                ),
                contentDescription = "",
                modifier = Modifier.size(24.dp)
            )
        }

        //Price
        SectionPrice(powerSource)

        SectionRating(powerSource = powerSource, onClick = onReviews)

        SectionMedia(
            media = powerSource.media.orEmpty(),
            onClick = onMediaClick
        )
    }
}

@Composable
private fun SectionMedia(
    media: List<Media>,
    onClick: () -> Unit
) {
    if (media.isNotEmpty()) MyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .horizontalScroll(rememberScrollState())
            .onClick(onClick),
        spacing = 16.dp
    ) {
        media.forEach {
            ItemMedia(image = it)
        }
    }
}

@Composable
private fun ItemMedia(image: Media) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(8.dp)
    ) {
        NetworkImage(
            src = image.url,
            description = image.title,
            modifier = Modifier.size(80.dp),
            scale = ContentScale.Crop
        )
    }
}

@Composable
internal fun SectionRating(
    powerSource: PowerSource,
    onClick: (() -> Unit)? = null
) {
    //Rating
    MyRow(
        modifier = Modifier
            .fillMaxWidth()
            .onClick(onClick, enabled = onClick != null),
        spacing = 8.dp
    ) {
        StationIcon(label = "%.1f".format(Locale.US, powerSource.rating))
        RatingBar(
            modifier = Modifier.weight(1f),
            rating = powerSource.rating
        )
        if (onClick != null) Icon(
            painter = painterResource(R.drawable.arrow_right),
            contentDescription = "",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun SectionCharging(
    powerSource: PowerSource,
    balance: String,
    onCharge: () -> Unit,
    uiEvents: (SourceEvents) -> Unit
) {
    MySurfaceColumn(
        modifier = Modifier.padding(16.dp),
        color = Color.White,
        border = 1.dp.asBorder,
        spacing = 8.dp,
        horizontalAlignment = Alignment.End
    ) {

        // Balance
        MyRow(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .height(IntrinsicSize.Min)
                .clickable { uiEvents(SourceEvents.Balance) },
            spacing = 0.dp
        ) {

            StationIcon(icon = R.drawable.ic_sd_wallet)

            Spacer(modifier = Modifier.weight(1f))

            LayoutDirectionLtr {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = balance,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = powerSource.currency,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(id = R.drawable.arrow_right),
                contentDescription = "",
                modifier = Modifier.size(24.dp)
            )
        }

        ButtonLarge(
            modifier = Modifier.fillMaxWidth(),
            onClick = onCharge,
            enabled = { powerSource.isOpen && powerSource.isAvailable },
            layoutDirection = LayoutDirection.Rtl,
            text = stringResource(id = R.string.station_charging_start),
            icon = R.drawable.charge,
            color = if (powerSource.isNear && powerSource.isAvailable) MyColors.white
            else MaterialTheme.colorScheme.secondary,
            background = if (powerSource.isNear && powerSource.isAvailable) MaterialTheme.colorScheme.secondary
            else MyColors.buttonColor
        )
        ButtonSmall(
            text = stringResource(R.string.station_charging_how_to),
            icon = R.drawable.outline_info_24,
            layoutDirection = LayoutDirection.Rtl,
            padding = PaddingValues(horizontal = 8.dp),
            color = Color.White,
            background = MaterialTheme.colorScheme.secondary,
            height = 30.dp,
            onClick = { uiEvents(SourceEvents.HowToCharge) }
        )
    }
}


@Composable
internal fun SectionLocation(
    powerSource: PowerSource,
    showMaxPower: Boolean = false,
    onDriveToStation: () -> Unit
) {
    MySurfaceColumn(
        modifier = Modifier.padding(16.dp),
        color = Color.White,
        border = 1.dp.asBorder,
        spacing = 8.dp
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            MyRow(spacing = 0.dp) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_turn_up_right),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(
                        R.string.station_km_away,
                        powerSource.distance()
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.charge),
                    contentDescription = "",
                    modifier = Modifier.size(24.dp)
                )
                if (showMaxPower) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(
                            id = R.string.station_kwh_value,
                            powerSource.maxConnector?.maxPower ?: 0
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = stringResource(id = R.string.station_max),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        ButtonLarge(
            modifier = Modifier.fillMaxWidth(),
            layoutDirection = LayoutDirection.Rtl,
            text = stringResource(id = R.string.station_drive),
            icon = R.drawable.baseline_near_me_24,
            onClick = onDriveToStation,
            color = if (powerSource.isNear.not() && powerSource.isAvailable) Color.White
            else MaterialTheme.colorScheme.secondary,
            background = if (powerSource.isNear.not() && powerSource.isAvailable) MaterialTheme.colorScheme.secondary
            else MyColors.buttonColor
        )

        Text(
            text = powerSource.address?.detailedAddress.orEmpty(),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun SectionConnectors(connectors: List<Connector>) {
    MySurfaceRow(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp, horizontal = 16.dp),
        color = Color.White,
        border = 1.dp.asBorder,
        spacing = 8.dp,
    ) {
        connectors.forEach { connector ->
            ItemConnector(connector)
        }
    }
}

//////////////////////

@Composable
private fun SectionDetails(powerSource: PowerSource) {
    MySurfaceColumn(
        modifier = Modifier.padding(16.dp),
        color = Color.White,
        spacing = 16.dp,
        border = 1.dp.asBorder,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(
                id = R.string.station_number,
                powerSource.id
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
        ItemDetails(
            title = stringResource(id = R.string.station_details_parking),
            value = "Free"
        )

        SectionAmenities(powerSource)

        ItemDetails(
            title = stringResource(id = R.string.station_details_descriptions),
            value = powerSource.description.orEmpty()
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SectionAmenities(powerSource: PowerSource) {
    MyColumn(spacing = 8.dp) {
        Text(
            text = stringResource(id = R.string.station_details_amenities),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            powerSource.amenities?.forEach {
                ItemAmenity(amenity = it)
            }
        }
    }
}

@Composable
private fun ItemAmenity(amenity: Amenity) {
    MyRow(spacing = 4.dp) {
        NetworkImage(
            src = amenity.icon.orEmpty(),
            placeHolder = R.drawable.logo,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = amenity.name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}


@Composable
private fun SectionPrice(powerSource: PowerSource) {
    val price = powerSource.price ?: return
    MyRow(modifier = Modifier.fillMaxWidth(), spacing = 4.dp) {
        StationIcon(icon = R.drawable.dollar_symbol)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            "%.2f".format(Locale.US, price),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            powerSource.currency,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary
        )
        val chargingUnit = stringResource(
            if (powerSource.isTimedPrice) R.string.station_per_minute
            else R.string.station_per_kwh
        )
        Text(
            chargingUnit.replaceFirstChar(Char::lowercase),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        powerSource.sessionLimit?.let {
            if (it > 0) Text(
                text = stringResource(R.string.station_max_value, it),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}


@Composable
private fun ItemDetails(
    title: String,
    value: String,
) {
    MyColumn(spacing = 4.dp) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        MyRow(spacing = 0.dp) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MyColors.subColor
            )
        }
    }
}

