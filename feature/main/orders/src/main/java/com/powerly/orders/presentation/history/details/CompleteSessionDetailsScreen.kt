package com.powerly.orders.presentation.history.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.powerly.orders.presentation.SessionsViewModel
import com.powerly.ui.dialogs.MyScreenBottomSheet

private const val TAG = "CompleteSessionDetailsScreen"

@Composable
fun CompleteSessionDetailsScreen(
    viewModel: SessionsViewModel,
    onBack: () -> Unit
) {
    val session by remember { viewModel.selectedSession }
    if (session != null) {
        MyScreenBottomSheet(onDismiss = onBack) {
            CompleteSessionDetailsContent(
                session = session!!,
                onClose = onBack,
            )
        }
    }
}
