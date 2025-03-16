package com.powerly.powerSource.boarding

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.powerly.lib.managers.StorageManager
import com.powerly.ui.dialogs.MyScreenBottomSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun OnBoardingDialog(
    viewModel: OnBoardingViewModel = hiltViewModel(),
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

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val storageManager: StorageManager
) : ViewModel() {
    fun setBoarding() {
        storageManager.showOnBoarding = false
    }
}