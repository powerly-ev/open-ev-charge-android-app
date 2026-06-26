package com.powerly.powersource.charging.presentation.session

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.powerly.core.domain.model.ChargingStatus
import com.powerly.core.domain.model.powerly.Session
import com.powerly.resources.R
import com.powerly.ui.dialogs.alert.MyAlertDialog
import com.powerly.ui.dialogs.alert.rememberAlertDialogState
import com.powerly.ui.dialogs.loading.rememberScreenState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChargingScreen(
    viewModel: ChargeViewModel = koinViewModel(),
    openSessionHistory: (Session) -> Unit,
    openActiveSessions: () -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val screenState = rememberScreenState()
    val stopChargingDialog = rememberAlertDialogState()
    val timerState = remember { viewModel.timerState }
    val session by remember { viewModel.session }

    BackHandler {
        if (session != null) openActiveSessions()
        else onBack()
    }

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
                    screenState.showMessage(state.message) {
                        onBack()
                    }
                }

                else -> {}
            }
        }
    }

    LifecycleResumeEffect(LocalLifecycleOwner) {
        coroutineScope.launch {
            viewModel.initSession()
        }
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
