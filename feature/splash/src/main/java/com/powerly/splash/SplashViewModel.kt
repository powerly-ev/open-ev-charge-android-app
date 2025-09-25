package com.powerly.splash

import androidx.lifecycle.ViewModel
import com.powerly.core.data.repositories.AppRepository
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.api.ApiErrorConstants
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.network.DeviceHelper
import com.powerly.lib.managers.CountryManager
import com.powerly.lib.managers.StorageManager
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class SplashViewModel (
    private val userRepository: UserRepository,
    private val appRepository: AppRepository,
    private val deviceHelper: DeviceHelper,
    private val storageManager: StorageManager,
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
                return if (storageManager.isLoggedIn) getUserDetails()
                else SplashAction.OpenWelcomeScreen
            }

            else -> null
        }
    }

    suspend fun getUserDetails(): SplashAction? {
        val it = userRepository.getUserDetails()
        return when (it) {
            is ApiStatus.Error -> {
                when (it.msg.code) {
                    ApiErrorConstants.MAINTENANCE_MODE -> {
                        SplashAction.Maintenance
                    }

                    ApiErrorConstants.UPGRADE_REQUIRED -> {
                        SplashAction.UpdateApp
                    }

                    ApiErrorConstants.TOO_MANY_REQUESTS -> {
                        SplashAction.TryAgain(it.msg.msg)
                    }

                    ApiErrorConstants.UNAUTHENTICATED -> {
                        storageManager.logOutAll()
                        SplashAction.OpenWelcomeScreen
                    }

                    else -> {
                        SplashAction.TryAgain(it.msg.msg)
                    }
                }
            }

            is ApiStatus.Success -> {
                val user = it.data
                storageManager.userDetails = user
                SplashAction.OpenHomeScreen
            }

            else -> null
        }
    }


    val appLink: String get() = deviceHelper.appLink
    val appVersion: String get() = deviceHelper.appVersion
    suspend fun isOnline() = deviceHelper.isOnline()
}