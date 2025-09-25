package com.powerly.lib.di

import android.app.Application
import android.content.Context
import com.powerly.core.analytics.EventsManager
import com.powerly.core.analytics.UserIdentifier
import com.powerly.core.analyticsImpl.EventsManagerImpl
import com.powerly.core.network.DeviceHelper
import com.powerly.lib.managers.StorageManager
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
        storageManager: StorageManager,
        deviceHelper: DeviceHelper
    ): EventsManager {
        val identifier: UserIdentifier? = storageManager.userDetails?.let { userDetails ->
            UserIdentifier(
                id = userDetails.id.toString(),
                name = userDetails.fullName,
                email = userDetails.email
            )
        }
        return EventsManagerImpl(
            context = applicationContext,
            isDebug = deviceHelper.isDebug,
            identifier = identifier
        )
    }
}
