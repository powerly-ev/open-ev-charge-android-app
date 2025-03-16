package com.powerly.account

import androidx.compose.runtime.Composable
import com.powerly.ui.HomeUiState


private const val TAG = "AccountScreen"

/**
 * Displays the Account screen.
 *
 * This composable displays the user's account information and provides
 * actions for managing their account. It also handles navigation to other
 * screens, such as Power Center, Vehicles, Invite, Profile, Support, and Wallet.
 *
 * @param uiState The current UI state of the Home screen.
 */
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
