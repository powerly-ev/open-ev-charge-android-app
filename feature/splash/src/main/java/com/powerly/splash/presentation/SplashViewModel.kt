package com.powerly.splash.presentation

import androidx.lifecycle.ViewModel
import com.powerly.core.domain.model.AppInfo
import com.powerly.splash.domain.model.SplashAction
import com.powerly.splash.domain.use_case.LoadCountriesUseCase
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SplashViewModel(
    private val loadCountriesUseCase: LoadCountriesUseCase,
    private val appInfo: AppInfo
) : ViewModel() {

    suspend fun loadCountries(): SplashAction? = loadCountriesUseCase()

    val appLink: String get() = appInfo.appLink
    val appVersion: String get() = appInfo.appVersion
    suspend fun isOnline() = appInfo.isOnline()
}
