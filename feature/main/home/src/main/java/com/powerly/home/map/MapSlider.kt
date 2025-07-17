package com.powerly.home.map

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.core.model.powerly.Amenity
import com.powerly.core.model.powerly.Connector
import com.powerly.core.model.powerly.PowerSource
import com.powerly.core.model.location.Target
import com.powerly.core.model.powerly.SourceType
import com.powerly.resources.R
import com.powerly.ui.containers.MyCardColum
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.containers.MyRow
import com.powerly.ui.components.MyTextDynamic
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
import java.util.Locale

private const val TAG = "MapSlider"

@Preview
@Composable
private fun MapSlidePreview() {
    val ps = PowerSource(
        title = "My Power Source",
        rating = 3.5,
        distance = 4.5,
        sourceType = SourceType(0, name = "C Type"),
        connectors = listOf(Connector(maxPower = 60.0, name = "Mennekes")),
        amenities = listOf(Amenity(id = 1, name = "Park")),
        inUse = false,
        reserved = 0,
        onlineStatus = 1,
        isExternal = false,
        latitude = 25.0,
        longitude = 23.0,
    )

    val userLocation = Target(
        latitude = 25.0001,
        longitude = 23.0001,
    )

    AppTheme {
        MyColumn {
            // available
            ItemMapPowerSource(powerSource = ps)
            // offline
            ItemMapPowerSource(ps.copy(onlineStatus = 0))
            // busy
            ItemMapPowerSource(ps.copy(inUse = true))
            // busy by you
            ItemMapPowerSource(ps.copy(inUse = true, isInUseByYou = true))
            // booked
            ItemMapPowerSource(ps.copy(reserved = 1))
            // booked by you
            ItemMapPowerSource(ps.copy(reserved = 1, isReservedByYou = true))
        }
    }
}

@Composable
internal fun PowerSourcesSlider(
    pagerState: PagerState,
    powerSources: () -> List<PowerSource>,
    onOpenPowerSource: (PowerSource) -> Unit,
    onSelectPowerSource: (PowerSource) -> Unit
) {
    val sources = powerSources()
    var isDragged by remember { mutableStateOf(false) }
    val dragState = pagerState.interactionSource.collectIsDraggedAsState()

    fun currentPowerSource(index: Int): PowerSource? =
        sources.getOrNull(index)

    LaunchedEffect(dragState.value) {
        if (dragState.value) {
            isDragged = true
        }
    }

    LaunchedEffect(pagerState.settledPage) {
        if (isDragged) {
            currentPowerSource(pagerState.settledPage)?.let {
                onSelectPowerSource(it)
            }
            isDragged = false
        }
    }

    HorizontalPager(
        state = pagerState,
        pageSpacing = 16.dp
    ) { pageIndex ->
        currentPowerSource(pageIndex)?.let {
            ItemMapPowerSource(
                powerSource = it,
                onClick = { onOpenPowerSource(it) },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ItemMapPowerSource(
    powerSource: PowerSource,
    onClick: () -> Unit = {}
) {
    MyCardColum(
        spacing = 4.dp,
        borderStroke = null,
        padding = PaddingValues(16.dp),
        fillMaxWidth = true,
        horizontalAlignment = Alignment.Start,
        onClick = onClick
    ) {

        //title
        MyRow(
            verticalAlignment = Alignment.CenterVertically,
            spacing = 4.dp
        ) {
            Icon(
                painterResource(
                    id = if (powerSource.isAvailable) R.drawable.checked_sign
                    else if (powerSource.isOffline) R.drawable.station_offline
                    else R.drawable.station_busy
                ),
                contentDescription = "",
                tint = if (powerSource.isAvailable) MaterialTheme.colorScheme.primary
                else MyColors.red500,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = powerSource.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.height(8.dp))
        MyRow(verticalAlignment = Alignment.Top) {
            //features
            FlowRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                //status
                if (powerSource.isAvailable.not()) {
                    if (powerSource.isOffline) MapChip(
                        text = stringResource(R.string.station_unavailable),
                        image = R.drawable.unavailable
                    ) else if (powerSource.isReserved) MapChip(
                        text = stringResource(
                            if (powerSource.isReservedByYou)
                                R.string.station_status_self_booked
                            else
                                R.string.station_status_booked
                        ),
                        icon = R.drawable.station_busy,
                        iconTint = MyColors.red500
                    ) else if (powerSource.isInUse) MapChip(
                        text = stringResource(
                            if (powerSource.isInUseByYou)
                                R.string.station_status_self_charging
                            else
                                R.string.station_status_busy
                        ),
                        icon = R.drawable.station_busy,
                        iconTint = MyColors.red500
                    )
                }

                //distance
                MapChip(
                    text = stringResource(
                        id = R.string.station_km_value,
                        powerSource.distance()
                    )
                )
                //rating
                MapChip(
                    text = if (powerSource.rating == 0.0)
                        stringResource(R.string.station_new)
                    else "%.1f".format(powerSource.rating, Locale.US),
                    icon = R.drawable.ic_baseline_star_24
                )

                powerSource.maxConnector?.let {
                    //max power
                    MapChip(stringResource(R.string.station_kwh_value, it.maxPower))
                    //type
                    //MapChip(powerSource.sourceType?.name.orEmpty())
                    //connector name
                    MapChip(it.name)
                    //amenity
                    powerSource.amenities?.forEach { amenity ->
                        MapChip(amenity.name)
                    }
                }
            }

            // logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "",
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun MapChip(
    text: String,
    @DrawableRes icon: Int? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    @DrawableRes image: Int? = null
) {
    if (text.isEmpty()) return

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        MyRow(
            verticalAlignment = Alignment.CenterVertically,
            spacing = 4.dp,
            modifier = Modifier
                .height(25.dp)
                .padding(horizontal = 2.dp)
        ) {
            icon?.let {
                Icon(
                    painterResource(it),
                    contentDescription = "",
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            image?.let {
                Image(
                    painterResource(it),
                    contentDescription = "",
                    modifier = Modifier.size(20.dp)
                )
            }
            MyTextDynamic(
                text,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 4.dp),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}