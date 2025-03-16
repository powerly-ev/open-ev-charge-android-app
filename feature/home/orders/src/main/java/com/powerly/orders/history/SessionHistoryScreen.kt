package com.powerly.orders.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.powerly.core.model.powerly.Session
import com.powerly.orders.SessionViewModel

private const val TAG = "SessionHistoryScreen"

/**
 * Displays the Session History Screen, showing past charging sessions.
 *
 * This composable function provides a screen to display a paginated list of past charging sessions.
 * It supports displaying sessions for both users and owners, though this distinction is
 * handled within the `SessionViewModel` and the provided data. Users can interact with
 * the listed sessions by viewing detailed information or initiating a new recharge
 * at the same charging station.
 *
 * The screen utilizes the `SessionViewModel` to fetch the session data and the `LazyPagingItems`
 * for efficient list rendering. It also dynamically displays the currency used for
 * session costs.
 *
 * @param viewModel The [SessionViewModel] providing access to the session data and application state.
 *   It's responsible for fetching the completed orders and storing the current currency.
 * @param openPowerSource A lambda function that navigates to the Power Source Screen.
 *   It takes the `chargePointId` of the selected session as a parameter, enabling
 *   the user to start a new charging session at that location.
 * @param openSessionDetails A lambda function that navigates to the Session Details Screen.
 *   It takes a [Session] object as a parameter, allowing the user to view detailed
 *   information about a past charging session.
 *
 * @see SessionViewModel
 * @see Session
 */
@Composable
fun SessionHistoryScreen(
    viewModel: SessionViewModel,
    openPowerSource: (String) -> Unit,
    openSessionDetails: (Session) -> Unit
) {
    val currency = remember { viewModel.currency }

    SessionHistoryScreenContent(
        sessions = viewModel.completedOrders,
        refresh = viewModel.refresh,
        currency = currency,
        onRecharge = { openPowerSource(it.chargePointId) },
        onItemClick = openSessionDetails
    )
}
