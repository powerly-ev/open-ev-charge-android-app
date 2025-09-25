package com.powerly.core.data.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single


@Module
@ComponentScan("com.powerly.core.data")
class DataModule {
    @Single
    @Named("IO") // The qualifier name
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Single
    @Named("Default")
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}