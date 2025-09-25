package com.powerly.lib.managers

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.powerly.lib.MainScreen.setMainScreenHome
import com.powerly.lib.MainScreen.setMainScreenWelcome
import com.powerly.resources.R
import org.koin.core.annotation.Single
import java.util.Locale


/**
 * Manages the application's locale and language settings.
 *
 * This class provides methods for initializing, changing, and retrieving
 * language settings for the application. It utilizes a [StorageManager]
 * to persist the selected language preference.
 *
 * It uses a map to store available languages and their corresponding codes.
 * The map is initialized from the `available_languages` and `available_language_codes`
 * string arrays defined in the resources.
 *
 * This class is designed as a singleton to ensure a single instance controls
 * the application's locale throughout its lifecycle.
 */
@Single
class LocaleManager(
    private val context: Context,
    private val storageManager: StorageManager
) {
    private val localeMap: HashMap<String, String> = HashMap()

    init {
        val names = context.resources.getStringArray(R.array.available_languages)
        val codes = context.resources.getStringArray(R.array.available_language_codes)
        for (i in names.indices) localeMap[codes[i]] = names[i]
    }

    val currentLanguage: String get() = storageManager.currentLanguage

    /**
     * change app language with any language code
     * and restart activity to apply changes..
     */
    fun changeLanguage(lang: String, activity: Activity) {
        storageManager.currentLanguage = lang
        setLocale(context, lang)
        restart(activity)
    }


    /**
     * Get current language name
     */
    fun getLanguageName(): String {
        val lang = storageManager.currentLanguage
        return if (localeMap.containsKey(lang)) localeMap[lang]!!
        else ""
    }

    //change app language with specific language code
    fun setLocale(context: Context, lang: String): Context {
        val locale = Locale.forLanguageTag(lang)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        config.fontScale = 1f //0.85 small size, 1 normal size, 1,15 big etc
        return context.createConfigurationContext(config)
    }

    // restart activity
    private fun restart(activity: Activity) {
        val intent: Intent = activity.intent
        if (storageManager.isLoggedIn) intent.setMainScreenHome()
        else intent.setMainScreenWelcome()
        activity.finish()
        activity.startActivity(intent)
    }


}