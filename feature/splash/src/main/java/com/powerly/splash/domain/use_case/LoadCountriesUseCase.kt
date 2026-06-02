package com.powerly.splash.domain.use_case

import com.powerly.core.data.repositories.AppRepository
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.api.ApiErrorConstants
import com.powerly.core.model.api.ApiStatus
import com.powerly.splash.domain.model.SplashAction
import org.koin.core.annotation.Single

/**
 * Initial splash bootstrap: refreshes the countries list and routes the user
 * to the welcome or home flow based on auth state. Surfaces maintenance and
 * forced-upgrade errors before any other navigation can happen.
 */
@Single
class LoadCountriesUseCase(
    private val appRepository: AppRepository,
    private val userRepository: UserRepository,
    private val getUserDetails: GetUserDetailsUseCase
) {
    suspend operator fun invoke(): SplashAction? {
        return when (val result = appRepository.updateCountries()) {
            is ApiStatus.Error -> {
                when (result.msg.code) {
                    ApiErrorConstants.MAINTENANCE_MODE -> SplashAction.Maintenance
                    ApiErrorConstants.UPGRADE_REQUIRED -> SplashAction.UpdateApp
                    else -> SplashAction.TryAgain(result.msg.value)
                }
            }

            is ApiStatus.Success -> {
                if (userRepository.isLoggedIn()) getUserDetails()
                else SplashAction.OpenWelcomeScreen
            }

            else -> null
        }
    }
}
