package com.powerly.user.welcome.language

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.powerly.lib.managers.LocaleManager
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class LanguagesViewModel (
    private val localeManager: LocaleManager
) : ViewModel() {

    val language: String get() = localeManager.currentLanguage
    val languageName: String get() = localeManager.getLanguageName()

    fun changeAppLanguage(lang: String, activity: Activity) {
        if (lang != localeManager.currentLanguage) {
            localeManager.changeLanguage(lang, activity)
        }
    }
}