package com.powerly.orders

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.powerly.core.model.powerly.OrderTab
import com.powerly.core.model.powerly.Session
import com.powerly.orders.active.SessionActiveScreen
import com.powerly.orders.history.SessionHistoryScreen
import com.powerly.resources.R
import com.powerly.ui.dialogs.loading.rememberScreenState
import org.koin.androidx.compose.koinViewModel

private const val TAG = "OrdersScreen"

/**
 * Displays the Orders screen, allowing users to view and manage their charging sessions.
 *
 * This composable function presents a tabbed interface with three sections:
 * - **Active:** Displays ongoing charging sessions, providing functionalities to
 * - **History:** Displays past charging sessions, enabling users to access historical
 *   session data and insights.
 *
 * Users can navigate between tabs using a tab bar at the top of the screen.
 * Each tab content is implemented using separate composable functions:
 * - `SessionActiveScreen`
 * - `SessionHistoryScreen`
 *
 * @param orderType The initial tab to display `Active`, or `History`.
 * @param sessionsViewModel The ViewModel that provides data and manages interactions for sessions.
 * @param openHistoryScreen A callback function to navigate to the history screen for a specific session.
 * @param openChargingScreen A callback function to navigate to the charging screen for a specific session.
 * @param openPowerSource A callback function to navigate to the power source screen.
 * @param openOrderDetails A callback function to navigate to the session details screen for a specific session.
 */
@Composable
fun OrdersScreen(
    orderType: Int,
    sessionsViewModel: SessionViewModel,
    openHistoryScreen: (Session) -> Unit,
    openChargingScreen: (Session) -> Unit,
    openPowerSource: (String) -> Unit,
    openOrderDetails: (Session) -> Unit
) {
    val activity = LocalActivity.current as ComponentActivity
    val chargerViewModel: ChargerViewModel = koinViewModel(viewModelStoreOwner = activity)
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
            OrderTab.ACTIVE -> {
                SessionActiveScreen(
                    screenState = screenState,
                    viewModel = sessionsViewModel,
                    chargerViewModel = chargerViewModel,
                    openHistoryScreen = openHistoryScreen,
                    openChargingScreen = openChargingScreen
                )
            }

            OrderTab.HISTORY -> {
                SessionHistoryScreen(
                    viewModel = sessionsViewModel,
                    openPowerSource = openPowerSource,
                    openSessionDetails = openOrderDetails
                )
            }
        }
    }
}
