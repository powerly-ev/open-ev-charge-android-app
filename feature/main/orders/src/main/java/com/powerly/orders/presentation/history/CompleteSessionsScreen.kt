package com.powerly.orders.presentation.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.powerly.core.domain.model.powerly.Session
import com.powerly.orders.presentation.SessionsViewModel

private const val TAG = "CompleteSessionsScreen"

@Composable
fun CompleteSessionsScreen(
    viewModel: SessionsViewModel,
    openPowerSource: (String) -> Unit,
    openSessionDetails: (Session) -> Unit
) {
    val currency by viewModel.currency.collectAsState("")
    CompleteSessionsContent(
        sessions = viewModel.completedOrders,
        refresh = viewModel.refresh,
        currency = currency,
        onRecharge = { openPowerSource(it.chargePointId) },
        onItemClick = openSessionDetails
    )
}
