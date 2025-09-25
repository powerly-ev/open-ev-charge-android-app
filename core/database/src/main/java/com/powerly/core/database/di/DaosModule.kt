package com.powerly.core.database.di


import com.powerly.core.database.AppDatabase
import com.powerly.core.database.dao.CountriesDao
import org.koin.core.annotation.Single

@Single
fun provideCountriesDao(
    database: AppDatabase
): CountriesDao {
    return database.countriesDao()
}