package com.powerly.user.welcome.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.data.repositories.AppRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

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