package com.powerly.orders.history.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.powerly.orders.SessionViewModel
import com.powerly.ui.dialogs.MyScreenBottomSheet

private const val TAG = "DeliveredOrderScreen"

/**
 * Displays the Delivered Order Screen, showing details of a completed charging session.
 *
 * This composable function displays the details of a completed charging session, allowing users to
 * review the session information and navigate back. It also shows a progress dialog
 * while the selected session is being loaded.
 *
 * @param viewModel The [SessionViewModel] providing data for the screen.
 * @param onBack A lambda function to navigate back to the previous screen.
 */
@Composable
fun DeliveredOrderScreen(
    viewModel: SessionViewModel,
    onBack: () -> Unit
) {
    val session by remember { viewModel.selectedSession }
    if (session != null) {
        MyScreenBottomSheet(onDismiss = onBack) {
            DeliveredOrderScreenContent(
                session = session!!,
                onClose = onBack,
            )
        }
    }
}
