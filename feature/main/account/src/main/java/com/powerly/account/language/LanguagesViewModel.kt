package com.powerly.account.language

import androidx.lifecycle.ViewModel
import com.powerly.core.data.repositories.AppRepository
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.ui.dialogs.loading.LoadingState
import com.powerly.ui.dialogs.loading.initBasicScreenState
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class LanguagesViewModel(
    private val userRepository: UserRepository,
    private val appRepository: AppRepository
) : ViewModel() {

    val screenState = initBasicScreenState(LoadingState())
    val language = userRepository.languageFlow

    suspend fun updateAppLanguage(
        lang: String,
        onDismiss: () -> Unit
    ) {
        if (userRepository.isLoggedIn().not()) {
            appRepository.setLanguage(lang)
            return
        }
        screenState.loading = true
        val result = appRepository.updateAppLanguage(lang)
        screenState.loading = false
        when (result) {
            is ApiStatus.Success -> {
                onDismiss()
            }

            else -> {}
        }

    }
}