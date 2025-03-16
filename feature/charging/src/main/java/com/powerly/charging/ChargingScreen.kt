package com.powerly.charging

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.powerly.core.data.model.ChargingStatus
import com.powerly.core.model.powerly.Session
import com.powerly.resources.R
import com.powerly.ui.dialogs.alert.MyAlertDialog
import com.powerly.ui.dialogs.alert.rememberAlertDialogState
import com.powerly.ui.dialogs.loading.rememberScreenState

private const val TAG = "ChargingScreen"

/**
 * A Composable function that displays the charging screen.
 *
 * This screen allows users to monitor the charging process, view charging details,
 * and stop the charging session. It handles the charging lifecycle, including
 * starting, monitoring, and stopping the charging process. It also manages
 * real-time updates using socket events and updates the UI accordingly.
 *
 */
@Composable
fun ChargingScreen(
    viewModel: ChargeViewModel = hiltViewModel(),
    openActiveSessions: () -> Unit,
    openSessionHistory: (Session) -> Unit,
    onBack: () -> Unit
) {
    val screenState = rememberScreenState()
    val stopChargingDialog = rememberAlertDialogState()
    val timerState = remember { viewModel.timerState }
    val session by remember { viewModel.session }

    BackHandler {
        if (viewModel.hasActiveSession) openActiveSessions()
        else onBack()
    }


    // Observe the charging session status
    LaunchedEffect(Unit) {
        viewModel.chargingStatus.collect { state ->
            screenState.loading = state is ChargingStatus.Loading
            when (state) {
                is ChargingStatus.Stop -> {
                    screenState.showSuccess {
                        openSessionHistory(state.session)
                    }
                }

                is ChargingStatus.Error -> {
                    screenState.showMessage(state.msg) {
                        onBack()
                    }
                }

                else -> {}
            }
        }
    }

    LifecycleResumeEffect(LocalLifecycleOwner) {
        viewModel.startCharging()
        onPauseOrDispose {
            viewModel.release()
        }
    }

    MyAlertDialog(
        state = stopChargingDialog,
        title = stringResource(R.string.station_charging_stop),
        message = stringResource(R.string.station_charging_stop_msg),
        positiveButton = stringResource(R.string.yes),
        negativeButton = stringResource(R.string.no),
        positiveButtonClick = viewModel::stopCharging
    )

    ChargingScreenContent(
        screenState = screenState,
        timerState = timerState,
        session = session,
        onClose = openActiveSessions,
        onStop = { stopChargingDialog.show() }
    )
}