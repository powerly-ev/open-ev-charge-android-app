package com.powerly.orders.presentation

import com.powerly.navigation.OrderTabs

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.powerly.core.domain.model.powerly.Session
import com.powerly.orders.presentation.active.ActiveSessionsScreen
import com.powerly.orders.presentation.history.CompleteSessionsScreen
import com.powerly.resources.R
import com.powerly.ui.dialogs.loading.rememberScreenState

private const val TAG = "OrdersScreen"

@Composable
fun OrdersScreen(
    orderType: Int,
    sessionsViewModel: SessionsViewModel,
    openHistoryScreen: (Session) -> Unit,
    openChargingScreen: (Session) -> Unit,
    openPowerSource: (String) -> Unit,
    openOrderDetails: (Session) -> Unit
) {
    val screenState = rememberScreenState()

    val tabs = remember {
        listOf(
            OrderTab(title = R.string.sessions_active),
            OrderTab(title = R.string.sessions_history)
        )
    }

    val pagerState: PagerState = rememberPagerState(
        initialPage = orderType,
        pageCount = { tabs.size }
    )

    OrdersScreenContent(
        tabs = tabs,
        pagerState = pagerState,
        screenState = screenState
    ) {
        when (it) {
            OrderTabs.ACTIVE -> {
                ActiveSessionsScreen(
                    screenState = screenState,
                    viewModel = sessionsViewModel,
                    openHistoryScreen = openHistoryScreen,
                    openChargingScreen = openChargingScreen
                )
            }

            OrderTabs.HISTORY -> {
                CompleteSessionsScreen(
                    viewModel = sessionsViewModel,
                    openPowerSource = openPowerSource,
                    openSessionDetails = openOrderDetails
                )
            }
        }
    }
}
