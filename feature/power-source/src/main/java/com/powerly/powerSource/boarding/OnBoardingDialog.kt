package com.powerly.powerSource.boarding

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.data.repositories.AppRepository
import com.powerly.ui.dialogs.MyScreenBottomSheet
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
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

@KoinViewModel
class OnBoardingViewModel(
    private val appRepository: AppRepository
) : ViewModel() {
    fun setBoarding() {
        viewModelScope.launch {
            appRepository.showOnBoardingOnce()
        }
    }
}