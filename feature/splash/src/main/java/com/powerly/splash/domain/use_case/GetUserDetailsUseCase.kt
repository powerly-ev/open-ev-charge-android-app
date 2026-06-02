package com.powerly.splash.domain.use_case

import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.api.ApiErrorConstants
import com.powerly.core.model.api.ApiStatus
import com.powerly.splash.domain.model.SplashAction
import org.koin.core.annotation.Single

/**
 * Fetches the current user's profile and maps the outcome to the next
 * splash action. Clears local login data on auth errors so the user is sent
 * back to the welcome flow.
 */
@Single
class GetUserDetailsUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): SplashAction? {
        return when (val response = userRepository.getUserDetails()) {
            is ApiStatus.Error -> {
                when (response.msg.code) {
                    ApiErrorConstants.MAINTENANCE_MODE -> SplashAction.Maintenance
                    ApiErrorConstants.UPGRADE_REQUIRED -> SplashAction.UpdateApp
                    ApiErrorConstants.TOO_MANY_REQUESTS -> SplashAction.TryAgain(response.msg.value)
                    ApiErrorConstants.UNAUTHENTICATED,
                    ApiErrorConstants.ACCESS_DENIED -> {
                        userRepository.clearLoginData()
                        SplashAction.OpenWelcomeScreen
                    }

                    else -> SplashAction.TryAgain(response.msg.value)
                }
            }

            is ApiStatus.Success -> SplashAction.OpenHomeScreen

            else -> null
        }
    }
}
