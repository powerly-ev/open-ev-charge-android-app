package com.powerly.powersource.details.presentation.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.powerly.ui.dialogs.MyScreenBottomSheet
import org.koin.androidx.compose.koinViewModel

@Composable
fun MediaScreen(
    viewModel: MediaViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val media by viewModel.media.collectAsState()
    MyScreenBottomSheet(onDismiss = onBack) {
        MediaScreenContent(
            media = { media },
            onClose = onBack
        )
    }
}
