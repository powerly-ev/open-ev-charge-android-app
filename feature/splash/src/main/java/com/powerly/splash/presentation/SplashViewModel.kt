package com.powerly.splash.presentation

import androidx.lifecycle.ViewModel
import com.powerly.core.network.DeviceHelper
import com.powerly.splash.domain.model.SplashAction
import com.powerly.splash.domain.use_case.LoadCountriesUseCase
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SplashViewModel(
    private val loadCountriesUseCase: LoadCountriesUseCase,
    private val deviceHelper: DeviceHelper
) : ViewModel() {

    suspend fun loadCountries(): SplashAction? = loadCountriesUseCase()

    val appLink: String get() = deviceHelper.appLink
    val appVersion: String get() = deviceHelper.appVersion
    suspend fun isOnline() = deviceHelper.isOnline()
}
