package com.powerly.home.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.powerly.core.model.powerly.Connector
import com.powerly.core.model.powerly.PowerSource
import com.powerly.home.home.slider.SectionSlider
import com.powerly.powerSource.boarding.OnBoardingScreenContent
import com.powerly.resources.R
import com.powerly.ui.HomeUiState
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.components.ButtonSmall
import com.powerly.ui.components.MyTextDynamic
import com.powerly.ui.components.SectionBalance
import com.powerly.ui.containers.LayoutDirectionLtr
import com.powerly.ui.containers.MyCardColum
import com.powerly.ui.containers.MySurface
import com.powerly.ui.containers.MySurfaceRow
import com.powerly.ui.dialogs.loading.ScreenState
import com.powerly.ui.dialogs.loading.rememberBasicScreenState
import com.powerly.ui.extensions.asPadding
import com.powerly.ui.map.MapViewState
import com.powerly.ui.map.MyMapView
import com.powerly.ui.map.PowerSourceMarker
import com.powerly.ui.map.rememberMapState
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors

private const val TAG = "HomeScreen"

private val ps1 = PowerSource(
    id = "1",
    title = "Station1",
    distance = 0.02,
    inUse = false,
    reserved = 0,
    onlineStatus = 1,
    isExternal = false,
    connectors = listOf(
        Connector(
            name = "Connect 1",
            maxPower = 50.0
        )
    )
)
private val nearPowerSources = listOf(ps1, ps1, ps1)

@Preview
@Composable
private fun Home_LoggedIn() {
    AppTheme {
        HomeScreenContent(
            mapState = rememberMapState(
                initialLocation = null,
                locationEnabled = true
            ),
            uiState = HomeUiState().apply {
                isLoggedIn.value = true
                supportMap.value = true
            },
            nearStations = { nearPowerSources },
            selectedPowerSource = { null },
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun Home_LoggedIn_NoLocation() {
    AppTheme {
        HomeScreenContent(
            mapState = rememberMapState(
                initialLocation = null,
                locationEnabled = false
            ),
            uiState = HomeUiState().apply {
                isLoggedIn.value = true
                supportMap.value = true
            },
            nearStations = { nearPowerSources },
            selectedPowerSource = { null },
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun Home_Guest_NoLocation() {
    AppTheme {
        HomeScreenContent(
            mapState = rememberMapState(
                initialLocation = null,
                locationEnabled = false
            ),
            uiState = HomeUiState().apply {
                isLoggedIn.value = false
                supportMap.value = true
            },
            nearStations = { nearPowerSources },
            selectedPowerSource = { null },
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun Home_LoggedIn_NoMap() {
    AppTheme {
        HomeScreenContent(
            mapState = rememberMapState(
                initialLocation = null,
                locationEnabled = false
            ),
            uiState = HomeUiState().apply {
                isLoggedIn.value = true
                supportMap.value = false
            },
            nearStations = { nearPowerSources },
            selectedPowerSource = { null },
            uiEvents = {}
        )
    }
}

@Composable
internal fun HomeScreenContent(
    screenState: ScreenState = rememberBasicScreenState(),
    mapState: MapViewState,
    uiState: HomeUiState,
    selectedPowerSource: () -> PowerSource?,
    nearStations: () -> List<PowerSource>,
    uiEvents: (HomeEvents) -> Unit,
) {
    val isLoggedIn by remember { uiState.isLoggedIn }
    val hasLocation by remember { mapState.locationEnabled }
    val supportMap = remember { uiState.supportMap.value }

    MyScreen(
        header = {
            Header(
                loggedIn = isLoggedIn,
                balance = { uiState.balance.value },
                currency = { uiState.currency.value },
                onLogin = { uiEvents(HomeEvents.OnLogin) },
                onSupport = { uiEvents(HomeEvents.OnSupport) },
                onBalance = { uiEvents(HomeEvents.OnBalance) }
            )
        },
        background = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp),
        spacing = 8.dp,
        screenState = screenState
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        if (supportMap) SectionSlider(
            cornerRadius = 8.dp,
            onClick = { uiEvents(HomeEvents.SliderClick) }
        )
        if (hasLocation && supportMap) {
            SectionMap(
                mapState = mapState,
                selectedPowerSource = selectedPowerSource,
                powerSources = nearStations,
                onMapClick = { uiEvents(HomeEvents.OpenMap()) },
                onPowerSource = { uiEvents(HomeEvents.OpenMap(it)) },
            )
        } else if (supportMap.not()) {
            OnBoardingScreenContent(
                modifier = Modifier.padding(vertical = 8.dp),
                background = MaterialTheme.colorScheme.background,
                slideCornerRadius = 8.dp
            )
        } else {
            SectionEnableLocation {
                uiEvents(HomeEvents.RequestLocation)
            }
        }
    }
}

@Composable
private fun SectionEnableLocation(onRequestLocation: () -> Unit) {
    MyCardColum(
        background = MyColors.viewColor,
        padding = 16.dp.asPadding,
        horizontalAlignment = Alignment.CenterHorizontally,
        spacing = 32.dp
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Icon(
            painter = painterResource(id = R.drawable.location_disabled),
            contentDescription = "",
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.secondary
        )

        ButtonLarge(
            modifier = Modifier.fillMaxWidth(),
            onClick = onRequestLocation,
            text = stringResource(id = R.string.station_location_allow),
            icon = R.drawable.location_enable,
            background = MaterialTheme.colorScheme.secondary,
            layoutDirection = LayoutDirection.Rtl,
            color = MyColors.white
        )
    }
}

@Composable
private fun ColumnScope.SectionMap(
    mapState: MapViewState,
    selectedPowerSource: () -> PowerSource?,
    powerSources: () -> List<PowerSource>,
    onPowerSource: (PowerSource) -> Unit,
    onMapClick: () -> Unit,
) {
    var showMap by remember { mutableStateOf(false) }
    MySurface(
        surfaceModifier = Modifier.weight(1f),
        color = Color.White
    ) {
        MyMapView(
            mapState = mapState,
            onMapClick = onMapClick,
            onMapLongClick = onMapClick,
            onMapLoaded = { showMap = true }
        ) {
            powerSources().mapIndexed { index, ps ->
                PowerSourceMarker(
                    powerSource = ps,
                    selected = {
                        (index == 0 && selectedPowerSource() == null)
                                || selectedPowerSource()?.id == ps.id
                    },
                    showRating = mapState.isGoogle,
                    onClick = { onPowerSource(ps) }
                )
            }
        }
        if (!showMap) MapPlaceHolder()
    }
}

@Composable
internal fun MapPlaceHolder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .zIndex(10f)
    )
}

/**
 * Header
 */
@Composable
fun Header(
    loggedIn: Boolean,
    balance: () -> String,
    currency: () -> String,
    onLogin: () -> Unit,
    onSupport: () -> Unit,
    onBalance: () -> Unit
) {
    LayoutDirectionLtr {
        MySurfaceRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color.White,
            spacing = 4.dp,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // powerly logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                modifier = Modifier
                    .width(35.dp)
                    .height(35.dp),
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
            // powerly app name
            Box(Modifier.weight(1f)) {
                MyTextDynamic(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            //login button
            if (loggedIn.not()) {
                ButtonSmall(
                    text = stringResource(id = R.string.login_option_title),
                    color = Color.White,
                    cornerRadius = 8.dp,
                    height = 30.dp,
                    fontSize = 14.sp,
                    onClick = onLogin
                )
            }
            // support button
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
            // balance section
            if (loggedIn) SectionBalance(
                balance = balance.invoke(),
                currency = currency.invoke(),
                onClick = onBalance
            )
        }
    }
}