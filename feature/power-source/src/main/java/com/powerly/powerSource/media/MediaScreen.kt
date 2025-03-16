package com.powerly.powerSource.media

import androidx.compose.runtime.Composable
import com.powerly.powerSource.PsViewModel
import com.powerly.ui.dialogs.MyScreenBottomSheet

private const val TAG = "MediaScreen"

/**
 * Displays the media associated with a power source.
 *
 * @param viewModel The ViewModel responsible for providing the power source data, including its media.
 * @param onBack A callback function to handle the back navigation event, typically closing the media screen.
 */
@Composable
fun MediaScreen(
    viewModel: PsViewModel,
    onBack: () -> Unit
) {
    MyScreenBottomSheet(onDismiss = onBack) {
        MediaScreenContent(
            media = { viewModel.powerSource?.media.orEmpty() },
            onClose = onBack
        )
    }
}
