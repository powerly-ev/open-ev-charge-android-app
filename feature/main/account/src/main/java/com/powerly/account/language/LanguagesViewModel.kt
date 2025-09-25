package com.powerly.account.language

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.powerly.core.data.repositories.AppRepository
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.user.DeviceBody
import com.powerly.core.network.DeviceHelper
import com.powerly.lib.managers.LocaleManager
import com.powerly.lib.managers.StorageManager
import com.powerly.ui.dialogs.loading.LoadingState
import com.powerly.ui.dialogs.loading.initBasicScreenState
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class LanguagesViewModel (
    private val userRepository: UserRepository,
    private val appRepository: AppRepository,
    private val localeManager: LocaleManager,
    private val storageManager: StorageManager,
    private val deviceHelper: DeviceHelper
) : ViewModel() {

    val screenState = initBasicScreenState(LoadingState())

    val selectedLanguage: String get() = storageManager.currentLanguage

    /**
     * Updates the app's language and associated user data.
     *
     * This suspend function updates the application's language setting and performs related actions,
     * such as fetching updated user details, changing the locale, and updating network request parameters.
     *
     * @param lang The new language code to be set for the app.
     */
    suspend fun updateAppLanguage(
        activity: Activity,
        lang: String,
        onDismiss: () -> Unit
    ) {
        if (storageManager.isLoggedIn.not()) {
            localeManager.changeLanguage(lang, activity)
            return
        }
        screenState.loading = true
        val result = userRepository.getUserDetails()
        screenState.loading = false
        when (result) {
            is ApiStatus.Error -> {
                onDismiss()
            }

            is ApiStatus.Success -> {
                val userDetails = result.data
                storageManager.userDetails = userDetails
                storageManager.currency = userDetails.currency
                storageManager.clearCountry()
                localeManager.changeLanguage(lang, activity)
                val body = DeviceBody(
                    imei = storageManager.imei(),
                    lang = lang,
                    token = storageManager.messagingToken,
                    appVersion = deviceHelper.appVersion,
                    deviceModel = deviceHelper.deviceModel,
                    deviceVersion = deviceHelper.deviceVersion,
                    deiceType = deviceHelper.deviceType
                )
                appRepository.updateDevice(body)
            }

            else -> {}
        }
    }
}