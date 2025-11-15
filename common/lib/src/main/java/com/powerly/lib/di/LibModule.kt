package com.powerly.lib.di

import android.app.Application
import android.content.Context
import com.powerly.core.analytics.EventsManager
import com.powerly.core.analyticsImpl.EventsManagerImpl
import com.powerly.core.network.DeviceHelper
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.powerly.lib")
class LibModule {
    @Single
    fun provideContext(application: Application): Context = application

    @Single
    fun provideEventsManager(
        applicationContext: Context,
        deviceHelper: DeviceHelper
    ): EventsManager {
        return EventsManagerImpl(
            context = applicationContext,
            isDebug = deviceHelper.isDebug,
            identifier = null
        )
    }
}
