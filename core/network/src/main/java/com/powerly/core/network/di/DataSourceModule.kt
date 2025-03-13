package com.powerly.core.network.di

import com.powerly.core.network.RemoteDataSource
import com.powerly.core.network.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    fun provideRemoteDataSource(
        retrofit: RetrofitClient
    ): RemoteDataSource = retrofit.client
}
