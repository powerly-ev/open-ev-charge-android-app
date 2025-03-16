package com.powerly.lib.di

import android.content.Context
import com.powerly.core.network.DeviceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DeviceHelperModule {
    @Provides
    @Singleton
    fun provideDeviceHelper(
        @ApplicationContext context: Context
    ): DeviceHelper = DeviceHelper(context)
}