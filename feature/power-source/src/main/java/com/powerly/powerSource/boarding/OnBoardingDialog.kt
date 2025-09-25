package com.powerly.powerSource.boarding

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.ViewModel
import com.powerly.lib.managers.StorageManager
import com.powerly.ui.dialogs.MyScreenBottomSheet
import org.koin.android.annotation.KoinViewModel


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

@KoinViewModel
class OnBoardingViewModel (
    private val storageManager: StorageManager
) : ViewModel() {
    fun setBoarding() {
        storageManager.showOnBoarding = false
    }
}