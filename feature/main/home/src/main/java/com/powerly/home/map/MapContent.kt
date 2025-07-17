package com.powerly.home.map

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.powerly.core.model.location.Target
import com.powerly.core.model.powerly.Amenity
import com.powerly.core.model.powerly.Connector
import com.powerly.core.model.powerly.PowerSource
import com.powerly.home.home.MapPlaceHolder
import com.powerly.resources.R
import com.powerly.ui.map.MyMapScreen
import com.powerly.ui.map.PowerSourceMarker
import com.powerly.ui.components.ButtonSmall
import com.powerly.ui.components.SectionBalance
import com.powerly.ui.containers.LayoutDirectionLtr
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.map.MapActionButton
import com.powerly.ui.map.MapViewState
import com.powerly.ui.map.rememberMapState
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors

private const val TAG = "MapScreen"

@Preview
@Composable
private fun MapScreenPreview() {
    val ps = PowerSource(
        title = "My Power Source",
        rating = 3.5,
        distance = 4.5,
        connectors = listOf(
            Connector(maxPower = 30.0),
            Connector(maxPower = 25.0),
            Connector(maxPower = 60.0, name = "Mennekes")
        ),
        amenities = listOf(Amenity(id = 1, name = "Park")),
        inUse = false,
        reserved = 0,
        onlineStatus = 1,
        isExternal = false,
    )

    val nearSources = listOf(ps, ps, ps)
    val mapState = rememberMapState(Target(0.0, 0.0))
    AppTheme {
        MapScreenContent(
            mapState = mapState,
            pagerState = rememberPagerState(
                initialPage = 0,
                pageCount = { 0 }
            ),
            balance = { "40.0" },
            currency = { "SAR" },
            selectedPowerSource = { null },
            nearPowerSources = { nearSources },
            uiEvents = {},
        )
    }
}

@Composable
internal fun MapScreenContent(
    mapState: MapViewState,
    pagerState: PagerState,
    balance: () -> String,
    currency: () -> String,
    selectedPowerSource: () -> PowerSource?,
    nearPowerSources: () -> List<PowerSource>,
    uiEvents: (MapEvents) -> Unit,
) {
    Scaffold(
        topBar = {
            Header(
                balance = balance,
                currency = currency,
                onBack = { uiEvents(MapEvents.OnBack) },
                onBalance = { uiEvents(MapEvents.OpenBalance) },
                onSupport = { uiEvents(MapEvents.OpenSupport) }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .background(MyColors.white)
        ) {

            SectionMap(
                mapState = mapState,
                nearPowerSources = nearPowerSources,
                selectedPowerSource = selectedPowerSource,
                onClickStation = { ps, open ->
                    val event = if (open) MapEvents.OpenPowerSource(ps)
                    else MapEvents.SelectPowerSource(ps, false)
                    uiEvents(event)
                },
                loadPowerSources = { lat, lng ->
                    val target = Target(lat, lng)
                    uiEvents(MapEvents.NearPowerSources(target))
                }
            )
            LayoutDirectionLtr {
                MyColumn(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(2f)
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    SectionTools(
                        onSearch = { uiEvents(MapEvents.OnSearch) },
                        onLocation = { uiEvents(MapEvents.OnLocation) })
                    // power sources slider
                    PowerSourcesSlider(
                        pagerState = pagerState,
                        powerSources = nearPowerSources,
                        onOpenPowerSource = {
                            uiEvents(MapEvents.OpenPowerSource(it))
                        },
                        onSelectPowerSource = {
                            Log.v(TAG, "onSelectPowerSource")
                            uiEvents(MapEvents.SelectPowerSource(it, true))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(
    balance: () -> String,
    currency: () -> String,
    onBack: () -> Unit,
    onBalance: () -> Unit,
    onSupport: () -> Unit
) {
    LayoutDirectionLtr {
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(
                bottomStart = 8.dp,
                bottomEnd = 8.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .background(color = Color.White)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.close),
                    modifier = Modifier
                        .width(35.dp)
                        .height(35.dp)
                        .clickable(onClick = onBack),
                    contentDescription = "",
                    contentScale = ContentScale.Inside
                )

                Spacer(modifier = Modifier.weight(1f))
                ButtonSmall(
                    text = stringResource(id = R.string.home_help),
                    icon = R.drawable.ic_baseline_support_agent_24,
                    layoutDirection = LayoutDirection.Rtl,
                    cornerRadius = 8.dp,
                    padding = PaddingValues(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    background = MaterialTheme.colorScheme.surface,
                    height = 30.dp,
                    iconSize = 25.dp,
                    fontSize = 14.sp,
                    onClick = onSupport
                )
                SectionBalance(balance(), currency(), onBalance)
            }
        }
    }
}

@Composable
private fun SectionMap(
    mapState: MapViewState,
    selectedPowerSource: () -> PowerSource?,
    nearPowerSources: () -> List<PowerSource>,
    loadPowerSources: (Double, Double) -> Unit,
    onClickStation: (PowerSource, Boolean) -> Unit
) {
    val powerSources = nearPowerSources()
    var showMap by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        MyMapScreen(
            modifier = Modifier.fillMaxSize(),
            mapState = mapState,
            onMapClick = { },
            onCameraLongMove = loadPowerSources,
            onMapLoaded = { showMap = true }
        ) {
            powerSources.forEachIndexed { index, ps ->
                PowerSourceMarker(
                    animated = true,
                    zoom = { mapState.currentCameraZoom },
                    powerSource = ps,
                    selected = {
                        (index == 0 && selectedPowerSource() == null)
                                || selectedPowerSource()?.id == ps.id
                    },
                    onClick = {
                        val selected = selectedPowerSource()?.id == ps.id
                        onClickStation(ps, selected)
                    }
                )
            }
        }
        if (!showMap) MapPlaceHolder()
    }
}

@Composable
private fun SectionTools(
    onLocation: () -> Unit,
    onSearch: () -> Unit,
) {
    //search button
    MapActionButton(
        icon = R.drawable.ic_search,
        onClick = onSearch
    )

    //location button
    MapActionButton(
        icon = R.drawable.baseline_near_me_24,
        onClick = onLocation
    )
}


