package com.powerly.core.database

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import java.util.Locale

fun getDefaultLocale(): String = Locale.getDefault().language

@SuppressLint("DiscouragedApi")
fun getDefaultAppLocale(context: Context): String {
    return try {
        // Get the identifier for available_language_codes at common/resources/res/values/strings_locales.xml
        val arrayId = context.resources.getIdentifier(
            "available_language_codes",
            "array",
            context.packageName
        )

        if (arrayId != 0) {
            val availableCodes = context.resources.getStringArray(arrayId)
            availableCodes.firstOrNull() ?: "en"
        } else "en"

    } catch (e: Exception) {
        e.printStackTrace()
        "en"
    }
}

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )


fun createUserDataStore(context: Context): DataStore<Preferences> = createDataStore(
    producePath = { context.filesDir.resolve("user_storage.preferences_pb").absolutePath }
)

fun createAppDataStore(context: Context): DataStore<Preferences> = createDataStore(
    producePath = { context.filesDir.resolve("app_storage.preferences_pb").absolutePath }
)