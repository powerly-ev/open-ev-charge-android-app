package com.powerly.core.database.di

import com.powerly.core.database.AppDatabase
import com.powerly.core.database.dao.CountriesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {
    @Provides
    fun providesCountiesDao(
        database: AppDatabase,
    ): CountriesDao = database.countriesDao()
}