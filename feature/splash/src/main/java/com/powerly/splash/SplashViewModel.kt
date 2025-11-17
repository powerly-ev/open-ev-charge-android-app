package com.powerly.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import com.powerly.core.data.repositories.AppRepository
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.api.ApiErrorConstants
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.network.DeviceHelper
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class SplashViewModel(
    private val userRepository: UserRepository,
    private val appRepository: AppRepository,
    private val deviceHelper: DeviceHelper
) : ViewModel() {

    suspend fun loadCountries(): SplashAction? {
        return when (val it = appRepository.updateCountries()) {
            is ApiStatus.Error -> {
                when (it.msg.code) {
                    ApiErrorConstants.MAINTENANCE_MODE -> {
                        SplashAction.Maintenance
                    }

                    ApiErrorConstants.UPGRADE_REQUIRED -> {
                        SplashAction.UpdateApp
                    }

                    else -> {
                        SplashAction.TryAgain(it.msg.value)
                    }
                }
            }

            is ApiStatus.Success -> {
                return if (userRepository.isLoggedIn()) getUserDetails()
                else SplashAction.OpenWelcomeScreen
            }

            else -> null
        }
    }

    suspend fun getUserDetails(): SplashAction? {
        val response = userRepository.getUserDetails()
        Log.v(TAG, response.toString())
        return when (response) {
            is ApiStatus.Error -> {
                when (response.msg.code) {
                    ApiErrorConstants.MAINTENANCE_MODE -> {
                        SplashAction.Maintenance
                    }

                    ApiErrorConstants.UPGRADE_REQUIRED -> {
                        SplashAction.UpdateApp
                    }

                    ApiErrorConstants.TOO_MANY_REQUESTS -> {
                        SplashAction.TryAgain(response.msg.value)
                    }

                    ApiErrorConstants.UNAUTHENTICATED -> {
                        userRepository.clearLoginData()
                        SplashAction.OpenWelcomeScreen
                    }

                    else -> {
                        SplashAction.TryAgain(response.msg.value)
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

    companion object {
        private const val TAG = "SplashViewModel"

    }

}