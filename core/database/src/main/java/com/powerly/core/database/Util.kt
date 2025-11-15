package com.powerly.core.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import java.util.Locale

fun getDefaultLocale(): String = Locale.getDefault().language

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