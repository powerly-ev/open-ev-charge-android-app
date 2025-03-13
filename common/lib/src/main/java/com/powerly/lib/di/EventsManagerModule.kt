package com.powerly.lib.di

import android.content.Context
import com.powerly.core.analytics.EventsManager
import com.powerly.core.analytics.UserIdentifier
import com.powerly.core.analyticsImpl.EventsManagerImpl
import com.powerly.core.network.DeviceHelper
import com.powerly.lib.managers.StorageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object EventsManagerModule {
    @Provides
    @Singleton
    fun provideEventsManager(
        @ApplicationContext context: Context,
        storageManager: StorageManager,
        deviceHelper: DeviceHelper
    ): EventsManager {
        var identifier: UserIdentifier? = storageManager.userDetails?.let {
            UserIdentifier(
                id = it.id.toString(),
                name = it.fullName,
                email = it.email
            )
        }
        return EventsManagerImpl(
            context = context,
            isDebug = deviceHelper.isDebug,
            identifier = identifier
        )
    }
}