package com.powerly.core.network.di

import android.content.Context
import com.powerly.core.network.DeviceHelper
import com.powerly.core.network.R
import com.powerly.core.network.RemoteDataSource
import com.powerly.core.network.RetrofitClient
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.powerly.core.network")
class NetworkModule {
    @Single
    fun provideRetrofitClient(
        context: Context,
        deviceHelper: DeviceHelper
    ): RetrofitClient {
        return RetrofitClient(
            errorMessage = context.getString(R.string.no_internet_message),
            deviceHelper = deviceHelper
        )
    }

    @Single
    fun provideRemoteDataSource(
        retrofitClient: RetrofitClient
    ): RemoteDataSource {
        return retrofitClient.client
    }

    @Single
    fun provideDeviceHelper(context: Context) = DeviceHelper(context)
}
