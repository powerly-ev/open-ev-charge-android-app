package com.powerly

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.powerly.lib.managers.LocaleManager
import com.powerly.lib.managers.StorageManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var storageManager: StorageManager

    @Inject
    lateinit var localeManager: LocaleManager

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}