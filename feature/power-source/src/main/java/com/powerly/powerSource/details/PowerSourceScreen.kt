package com.powerly.powerSource.details

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import com.powerly.core.data.model.SourceStatus
import com.powerly.core.model.powerly.PowerSource
import com.powerly.powerSource.PsViewModel
import com.powerly.ui.HomeUiState
import com.powerly.ui.dialogs.loading.rememberScreenState
import com.powerly.ui.extensions.openUriSafely

private const val TAG = "PowerSourceScreen"

/**
 * Displays the details of a power source.
 *
 * This composable function retrieves the power source details based on the provided `powerSourceId`.
 * It displays the power source information, user's balance, and handles user interactions
 * such as calling the power source or navigating to it.
 *
 */
@Composable
fun PowerSourceScreen(
    powerSourceId: String,
    uiState: HomeUiState,
    viewModel: PsViewModel,
    onNavigate: (SourceEvents) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val screenState = rememberScreenState()
    val userLocation = remember { viewModel.userLocation() }
    var powerSource by remember { mutableStateOf<PowerSource?>(null) }
    val balance by remember { uiState.balance }

    LaunchedEffect(userLocation) {
        Log.i(TAG, "powerSourceId - $powerSourceId")

        if (viewModel.showOnBoardingOnce()) {
            onNavigate(SourceEvents.HowToCharge)
        }
        // load power source details
        viewModel.getPowerSource(
            id = powerSourceId,
            latitude = userLocation?.latitude,
            longitude = userLocation?.longitude,
        )
    }

    LaunchedEffect(Unit) {
        viewModel.powerSourceStatus.collect {
            screenState.loading = it is SourceStatus.Loading
            when (it) {
                is SourceStatus.Error -> {
                    screenState.showMessage(it.msg) {
                        onNavigate(SourceEvents.Close)
                    }
                }

                is SourceStatus.Success -> {
                    powerSource = it.powerSource
                }

                else -> {}
            }
        }
    }

    PowerSourceContent(
        screenState = screenState,
        powerSource = { powerSource },
        balance = balance,
        uiEvents = { event ->
            when (event) {
                is SourceEvents.CAll -> {
                    uriHandler.openUriSafely(event.contact.orEmpty())
                }

                is SourceEvents.DriveToStation -> {
                    val directionsUri =
                        "http://maps.google.com/maps?daddr=${powerSource?.latitude},${powerSource?.longitude}"
                    uriHandler.openUriSafely(directionsUri)
                }

                else -> onNavigate(event)
            }
        }
    )
}

