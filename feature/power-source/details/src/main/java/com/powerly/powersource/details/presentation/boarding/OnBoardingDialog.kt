package com.powerly.powersource.details.presentation.boarding

import androidx.compose.runtime.Composable
import com.powerly.ui.dialogs.MyScreenBottomSheet
import org.koin.androidx.compose.koinViewModel


@Composable
fun OnBoardingDialog(
    viewModel: OnBoardingViewModel = koinViewModel(),
    onDismiss: () -> Unit,
) {
    MyScreenBottomSheet(onDismiss = onDismiss) {
        OnBoardingScreenContent(
            onDone = {
                viewModel.setBoarding()
                onDismiss()
            }
        )
    }
}
