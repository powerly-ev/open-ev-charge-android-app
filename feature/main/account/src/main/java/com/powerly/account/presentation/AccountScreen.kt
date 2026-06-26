package com.powerly.account.presentation

import androidx.compose.runtime.Composable
import com.powerly.ui.HomeUiState


private const val TAG = "AccountScreen"

@Composable
fun AccountScreen(
    uiState: HomeUiState,
    uiEvents: (AccountEvents) -> Unit
) {
    AccountScreenContent(
        uiState = uiState,
        uiEvents = uiEvents
    )
}
