package com.powerly.core.network.di

import android.content.Context
import com.powerly.core.network.DeviceHelper
import com.powerly.core.network.KtorClient
import com.powerly.core.network.RemoteDataSource
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.powerly.core.network")
class NetworkModule {
    @Single
    fun provideNewRemoteDataSource(
        ktorClient: KtorClient
    ): RemoteDataSource {
        return RemoteDataSource(ktorClient.client)
    }

    @Single
    fun provideDeviceHelper(context: Context) = DeviceHelper(context)
}
