package com.powerly.lib.managers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import com.powerly.core.model.util.Item
import com.powerly.lib.MainScreen.setMainScreenHome
import com.powerly.lib.MainScreen.setMainScreenWelcome
import com.powerly.resources.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Manages the application's locale and language settings.
 * <p>
 * This class provides methods for initializing, changing, and retrieving
 * language settings for the application. It utilizes a {@link StorageManager}
 * to persist the selected language preference.
 * </p>
 * <p>
 * It uses a map to store available languages and their corresponding codes.
 * The map is initialized from the `available_languages` and `available_language_codes`
 * string arrays defined in the resources.
 * </p>
 * <p>
 * This class is designed as a singleton to ensure a single instance controls
 * the application's locale throughout its lifecycle.
 * </p>
 */
@Singleton
class LocaleManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storageManager: StorageManager
) {
    private val localeMap: HashMap<String, String> = HashMap()

    init {
        instance = this
        val names = context.resources.getStringArray(R.array.available_languages)
        val codes = context.resources.getStringArray(R.array.available_language_codes)
        for (i in names.indices) localeMap[codes[i]] = names[i]
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: LocaleManager
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
        val locale = Locale(lang)
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