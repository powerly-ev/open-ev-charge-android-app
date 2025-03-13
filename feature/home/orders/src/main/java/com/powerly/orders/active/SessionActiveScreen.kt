package com.powerly.orders.active

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.powerly.core.data.model.ChargingStatus
import com.powerly.core.model.powerly.Session
import com.powerly.orders.ChargerViewModel
import com.powerly.orders.SessionViewModel
import com.powerly.resources.R
import com.powerly.ui.dialogs.alert.MyAlertDialog
import com.powerly.ui.dialogs.alert.rememberAlertDialogState
import com.powerly.ui.dialogs.loading.ScreenState
import kotlinx.coroutines.launch


private const val TAG = "SessionActiveScreen"

/**
 * Displays the Session Active Screen, showing active charging sessions.
 *
 * This composable function retrieves and displays a paginated list of active charging sessions.
 * It allows users to view session details, and initiate the process to stop a charging session.
 * The screen distinguishes between different active sessions and presents them in a scrollable list.
 *
 * When a user initiates the stop charging action, a confirmation dialog is presented. Upon confirmation,
 * the function attempts to stop the charging process and navigates the user to the history screen if the
 * stop is successful.
 *
 * @param screenState The [ScreenState] object used to manage the overall state of the screen,
 *   including loading, success, and error states.
 * @param viewModel The [SessionViewModel] providing data related to active charging sessions.
 *   It is responsible for fetching and managing the list of sessions.
 * @param chargerViewModel The [ChargerViewModel] used to interact with charging operations,
 *   specifically for stopping a charging session.
 * @param openChargingScreen A lambda function that takes a [Session] as input and navigates
 *   the user to the Charging Screen, providing detailed information about the selected session.
 * @param openHistoryScreen A lambda function that takes a [Session] as input and navigates the
 *   user to the History Screen, typically after a session has been stopped.
 */
@Composable
fun SessionActiveScreen(
    screenState: ScreenState,
    viewModel: SessionViewModel,
    chargerViewModel: ChargerViewModel,
    openChargingScreen: (Session) -> Unit,
    openHistoryScreen: (Session) -> Unit
) {
    val dialogState = rememberAlertDialogState()
    val coroutineScope = rememberCoroutineScope()
    val currency = remember { viewModel.currency }
    var selectedSession by remember { mutableStateOf<Session?>(null) }

    /**
     * Stops the charging process for the given session.
     */
    fun stopCharging() {
        coroutineScope.launch {
            screenState.loading = true
            val result = chargerViewModel.stopCharging(selectedSession)
            screenState.loading = false
            when (result) {
                is ChargingStatus.Stop -> {
                    Log.i(TAG, "ChargingStatus.Stop")
                    screenState.showSuccess {
                        openHistoryScreen(result.session)
                    }
                }

                is ChargingStatus.Error -> {
                    screenState.showMessage(result.msg)
                }

                else -> {}
            }
        }
    }


    MyAlertDialog(
        state = dialogState,
        title = stringResource(R.string.station_charging_stop),
        message = stringResource(R.string.sessions_charging_stop_msg),
        positiveButtonClick = ::stopCharging,
        negativeButtonClick = {}
    )

    SessionActiveScreenContent(
        sessionsFlow = viewModel.activeOrders,
        autoRefresh = true,
        currency = currency,
        onItemClick = openChargingScreen,
        onStopCharging = {
            selectedSession = it
            dialogState.show()
        }
    )
}

