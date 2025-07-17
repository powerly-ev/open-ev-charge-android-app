package com.powerly.home.map

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.powerly.core.model.location.Target
import com.powerly.core.model.powerly.PowerSource
import com.powerly.home.MyMapViewModel
import com.powerly.ui.HomeUiState
import com.powerly.ui.dialogs.locationSearch.LocationSearchDialog
import com.powerly.ui.dialogs.rememberMyDialogState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "MapScreen"

/**
 * This composable function displays the main map screen of the application.
 * It shows the user's location, nearby power sources, and allows the user to interact with the map.
 *
 * @param viewModel The [MyMapViewModel] instance used to handle map-related logic.
 * @param uiState The [HomeUiState] instance containing the UI state of the home screen.
 * @param openPowerSource A lambda function that navigates to a power source details screen.
 * @param showBalance A lambda function that open Balance screen.
 * @param openSupport A lambda function that open chat screen.
 * @param onBack A lambda function that is called when the user presses the back button.
 */
@Composable
fun MapScreen(
    viewModel: MyMapViewModel,
    uiState: HomeUiState,
    openPowerSource: (PowerSource) -> Unit,
    showBalance: () -> Unit,
    openSupport: () -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val balance by remember { uiState.balance }
    val currency by remember { uiState.currency }
    val locationSearchDialog = rememberMyDialogState()
    val mapState = remember { viewModel.mapState }
    val userLocation by remember { viewModel.userLocation }
    val powerSources = remember { viewModel.nearPowerSources }
    var selectedPowerSource by remember { viewModel.selectedPowerSource }

    BackHandler(onBack = onBack)

    val pageState = rememberPagerState(
        initialPage = 0,
        pageCount = { powerSources.size }
    )

    fun selectPowerSource(
        powerSource: PowerSource,
        moveCamera: Boolean = false,
        updateMarkers: Boolean = false
    ) {
        if (moveCamera) mapState.moveCameraKeepZoom(powerSource.location)
        if (updateMarkers) mapState.updateMarkers()
        val index = powerSources.indexOfFirst { it.id == powerSource.id }
        Log.i(TAG, "selectPowerSource - ${powerSource.title} - index:$index")
        coroutineScope.launch {
            pageState.safeScrollTo(index)
        }
    }

    fun loadNearPowerSources(target: Target) {
        viewModel.loadPowerSources(
            latitude = target.latitude,
            longitude = target.longitude
        )
    }

    LaunchedEffect(Unit) {
        if (selectedPowerSource == null) {
            selectedPowerSource = powerSources.getOrNull(0)
        }
        selectedPowerSource?.let {
            delay(1000)
            selectPowerSource(it, moveCamera = true)
        }
    }


    LocationSearchDialog(
        state = locationSearchDialog,
        onSelectPlace = {
            Log.e(TAG, "loadPowerSources")
            loadNearPowerSources(it)
            mapState.moveCamera(it)
        }
    )

    fun uiEvents(it: MapEvents) {
        when (it) {
            is MapEvents.OpenPowerSource -> {
                val ps = it.powerSource
                selectedPowerSource = ps
                openPowerSource(ps)
            }

            is MapEvents.SelectPowerSource -> {
                val ps = it.powerSource
                if (ps.id != selectedPowerSource?.id) {
                    selectedPowerSource = ps.apply { this.currency = currency }
                    selectPowerSource(
                        powerSource = ps,
                        moveCamera = true,
                        updateMarkers = it.selectByScroll
                    )
                }
            }

            is MapEvents.NearPowerSources -> {
                Log.e(TAG, "loadPowerSources")
                loadNearPowerSources(it.target)
            }

            is MapEvents.OnBack -> {
                onBack()
            }

            is MapEvents.OnLocation -> {
                viewModel.updateMapLocation()
            }

            is MapEvents.OnSearch -> {
                locationSearchDialog.show()
            }

            is MapEvents.OpenBalance -> {
                showBalance()
            }

            is MapEvents.OpenSupport -> openSupport()
        }
    }

    MapScreenContent(
        mapState = mapState,
        pagerState = pageState,
        balance = { balance },
        currency = { currency },
        selectedPowerSource = { selectedPowerSource },
        nearPowerSources = { powerSources },
        uiEvents = ::uiEvents
    )
}

private suspend fun PagerState.safeScrollTo(index: Int) {
    try {
        this.animateScrollToPage(index)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

