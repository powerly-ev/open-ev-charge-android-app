package com.powerly.core.network.di

import android.content.Context
import com.powerly.core.network.DeviceHelper
import com.powerly.core.network.R
import com.powerly.core.network.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Provides
    @Singleton
    fun provideRetrofitClient(
        @ApplicationContext context: Context,
        deviceHelper: DeviceHelper
    ): RetrofitClient {
        return RetrofitClient(
            errorMessage = context.getString(R.string.no_internet_message),
            deviceHelper = deviceHelper
        )
    }
}