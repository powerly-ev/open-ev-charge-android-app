package com.powerly.account.language

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.powerly.core.data.repositories.AppRepository
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.lib.managers.LocaleManager
import com.powerly.ui.dialogs.loading.LoadingState
import com.powerly.ui.dialogs.loading.initBasicScreenState
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class LanguagesViewModel(
    private val userRepository: UserRepository,
    private val appRepository: AppRepository,
    private val localeManager: LocaleManager,
) : ViewModel() {

    val screenState = initBasicScreenState(LoadingState())

    val selectedLanguage: String get() = appRepository.getLanguage()

    suspend fun updateAppLanguage(
        activity: Activity,
        lang: String,
        onDismiss: () -> Unit
    ) {
        if (userRepository.isLoggedIn.not()) {
            localeManager.changeLanguage(lang, activity)
            return
        }
        screenState.loading = true
        val result = appRepository.updateAppLanguage(lang)
        screenState.loading = false
        when (result) {
            is ApiStatus.Error -> {
                onDismiss()
            }

            is ApiStatus.Success -> {
                localeManager.changeLanguage(lang, activity)
            }

            else -> {}
        }
    }
}