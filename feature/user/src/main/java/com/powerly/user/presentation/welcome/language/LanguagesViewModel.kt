package com.powerly.user.presentation.welcome.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.domain.repository.AppRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class LanguagesViewModel(
    private val appRepository: AppRepository
) : ViewModel() {

    val language = appRepository.languageFlow

    fun changeAppLanguage(lang: String) {
        viewModelScope.launch(NonCancellable) {
            if (lang != appRepository.getLanguage()) {
                appRepository.setLanguage(lang)
            }
        }
    }
}
