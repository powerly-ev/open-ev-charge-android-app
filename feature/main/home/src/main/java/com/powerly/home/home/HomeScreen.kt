package com.powerly.home.home

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.powerly.core.data.model.ActivityResultState
import com.powerly.home.MyMapViewModel
import com.powerly.home.home.NavigationEvents.Login
import com.powerly.home.home.NavigationEvents.Map
import com.powerly.home.home.NavigationEvents.SourceDetails
import com.powerly.ui.HomeUiState
import com.powerly.ui.dialogs.loading.LoadingState
import com.powerly.ui.dialogs.loading.rememberBasicScreenState
import com.powerly.ui.dialogs.rememberMyDialogState
import com.powerly.ui.dialogs.signIn.SignInOptionsDialog
import com.powerly.ui.util.rememberPermissionsState
import kotlinx.coroutines.launch


private const val TAG = "HomeScreen"

/**
 * Represents the home screen of the application.
 *
 * This composable displays the main map view, along with various UI elements
 * for interacting with the map and power sources. It handles user interactions,
 * location updates, and navigation to other screens.
 *
 */
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    activityResult: ActivityResultState,
    mapViewModel: MyMapViewModel,
    navigate: (NavigationEvents) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val screenState = rememberBasicScreenState(LoadingState())
    val signInOptionsDialog = rememberMyDialogState()

    val isLoggedIn by remember { uiState.isLoggedIn }
    var doOnce by rememberSaveable { mutableStateOf(true) }

    val permissionsState = rememberPermissionsState(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val mapState = remember { mapViewModel.mapState }
    val nearPowerSources = remember { mapViewModel.nearPowerSources }
    var selectedPowerSource by remember { mapViewModel.selectedPowerSource }

    fun initMapAndLocation(requestManually: Boolean = false) {
        Log.v(TAG, "initMapAndLocation")
        mapViewModel.requestLocationServices(
            permissionsState = permissionsState,
            activityResult = activityResult,
            requestManually = requestManually,
            onAllowed = {
                coroutineScope.launch {
                    screenState.loading = true
                    mapViewModel.initMap()
                    if (isLoggedIn) mapViewModel.loadNearPowerSources()
                    screenState.loading = false
                }
            }
        )
    }

    // open a power source passed in a deep link
    LaunchedEffect(Unit) {
        selectedPowerSource?.let {
            mapState.moveCameraKeepZoom(it.location)
        }
    }

    LaunchedEffect(Unit) {
        if (doOnce) {
            doOnce = false
            initMapAndLocation(requestManually = false)
            // request notification permission if not granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permission = Manifest.permission.POST_NOTIFICATIONS
                if (permissionsState.isItGranted(permission).not()) {
                    permissionsState.requestSpecific(permission) {}
                }
            }
        }
    }


    /**
     * Sign-in options bottom sheet dialog
     */
    SignInOptionsDialog(
        state = signInOptionsDialog,
        showGuest = false,
        uiEvents = { navigate(Login(it)) }
    )

    fun uiEvents(event: HomeEvents) {
        when (event) {
            is HomeEvents.OpenPowerSource -> {
                navigate(SourceDetails(event.source))
            }

            is HomeEvents.OpenMap -> {
                if (isLoggedIn) navigate(Map(event.source))
                else signInOptionsDialog.show()
            }

            is HomeEvents.RequestLocation -> {
                initMapAndLocation(requestManually = true)
            }

            is HomeEvents.OnLogin -> signInOptionsDialog.show()
            is HomeEvents.OnSupport -> navigate(NavigationEvents.Support)
            is HomeEvents.OnBalance -> navigate(NavigationEvents.Balance)
            is HomeEvents.SliderClick -> {}
        }
    }

    HomeScreenContent(
        mapState = mapState,
        screenState = screenState,
        uiState = uiState,
        selectedPowerSource = { selectedPowerSource },
        nearStations = { nearPowerSources },
        uiEvents = ::uiEvents
    )
}

