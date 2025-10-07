package com.powerly.splash

import androidx.lifecycle.ViewModel
import com.powerly.core.data.repositories.AppRepository
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.api.ApiErrorConstants
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.network.DeviceHelper
import com.powerly.lib.managers.CountryManager
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class SplashViewModel(
    private val userRepository: UserRepository,
    private val appRepository: AppRepository,
    private val deviceHelper: DeviceHelper,
    private val countryManager: CountryManager
) : ViewModel() {

    suspend fun loadCountries(): SplashAction? {
        val it = appRepository.getCountries()
        return when (it) {
            is ApiStatus.Error -> {
                when (it.msg.code) {
                    ApiErrorConstants.MAINTENANCE_MODE -> {
                        SplashAction.Maintenance
                    }

                    ApiErrorConstants.UPGRADE_REQUIRED -> {
                        SplashAction.UpdateApp
                    }

                    else -> {
                        SplashAction.TryAgain(it.msg.msg)
                    }
                }
            }

            is ApiStatus.Success -> {
                countryManager.initCountries(it.data)
                return if (userRepository.isLoggedIn) getUserDetails()
                else SplashAction.OpenWelcomeScreen
            }

            else -> null
        }
    }

    suspend fun getUserDetails(): SplashAction? {
        return when (val response = userRepository.getUserDetails()) {
            is ApiStatus.Error -> {
                when (response.msg.code) {
                    ApiErrorConstants.MAINTENANCE_MODE -> {
                        SplashAction.Maintenance
                    }

                    ApiErrorConstants.UPGRADE_REQUIRED -> {
                        SplashAction.UpdateApp
                    }

                    ApiErrorConstants.TOO_MANY_REQUESTS -> {
                        SplashAction.TryAgain(response.msg.msg)
                    }

                    ApiErrorConstants.UNAUTHENTICATED -> {
                        userRepository.clearLoginData()
                        SplashAction.OpenWelcomeScreen
                    }

                    else -> {
                        SplashAction.TryAgain(response.msg.msg)
                    }
                }
            }

            is ApiStatus.Success -> {
                SplashAction.OpenHomeScreen
            }

            else -> null
        }
    }


    val appLink: String get() = deviceHelper.appLink
    val appVersion: String get() = deviceHelper.appVersion
    suspend fun isOnline() = deviceHelper.isOnline()
}