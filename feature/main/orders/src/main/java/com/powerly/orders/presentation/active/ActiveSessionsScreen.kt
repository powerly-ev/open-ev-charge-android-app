package com.powerly.orders.presentation.active

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.Session
import com.powerly.orders.presentation.SessionsViewModel
import com.powerly.resources.R
import com.powerly.ui.dialogs.alert.MyAlertDialog
import com.powerly.ui.dialogs.alert.rememberAlertDialogState
import com.powerly.ui.dialogs.loading.ScreenState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


private const val TAG = "ActiveSessionsScreen"

@Composable
fun ActiveSessionsScreen(
    screenState: ScreenState,
    viewModel: SessionsViewModel,
    openChargingScreen: (Session) -> Unit,
    openHistoryScreen: (Session) -> Unit
) {
    val chargerViewModel: ActiveSessionsViewModel = koinViewModel()
    val dialogState = rememberAlertDialogState()
    val coroutineScope = rememberCoroutineScope()
    val currency by viewModel.currency.collectAsState("")
    var selectedSession by remember { mutableStateOf<Session?>(null) }

    fun stopCharging() {
        coroutineScope.launch {
            screenState.loading = true
            val result = chargerViewModel.stopCharging(selectedSession)
            screenState.loading = false
            when (result) {
                is ApiStatus.Success -> {
                    Log.i(TAG, "stopCharging success")
                    screenState.showSuccess {
                        openHistoryScreen(result.data)
                    }
                }

                is ApiStatus.Error -> {
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

    ActiveSessionsContent(
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
