package com.powerly.core.database

import com.powerly.core.database.dao.CountriesDao
import com.powerly.core.database.model.CountryEntity
import com.powerly.core.database.model.asCountry
import com.powerly.core.database.model.asEntity
import com.powerly.core.model.location.Country
import org.koin.core.annotation.Single

@Single
class LocalDataSource (
    private val countriesDao: CountriesDao
) {
    suspend fun getCountries(): List<Country>? {
        return try {
            if (countriesDao.getCount() > 0)
                countriesDao.getCountries().map(CountryEntity::asCountry)
            else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun insertCountries(countries: List<Country>) {
        try {
            countriesDao.insert(countries.map(Country::asEntity))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun clearCountry() {
        try {
            countriesDao.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}