package com.powerly.powersource.details.presentation.boarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.domain.repository.AppRepository
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

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
