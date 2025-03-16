package com.powerly.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.powerly.core.database.model.CountryEntity

/**
 * DAO for [CountryEntity] access.
 */
@Dao
interface CountriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(countries: List<CountryEntity>)

    @Query("SELECT * FROM countries")
    suspend fun getCountries(): List<CountryEntity>

    @Query("SELECT count(*) FROM countries")
    suspend fun getCount(): Int

    @Query("DELETE FROM countries")
    suspend fun clear()
}
