package com.powerly.core.domain.repository

import com.powerly.core.domain.model.CurrenciesStatus
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.location.Country
import kotlinx.coroutines.flow.Flow

interface AppRepository {

    suspend fun showOnBoardingOnce(): Boolean

    /**
     * Whether to show the registration notification.
     * Only shown for unregistered users.
     */
    suspend fun showRegisterNotification(): Boolean

    suspend fun updateCountries(): ApiStatus<List<Country>>

    suspend fun getUserCountry(): Country?

    suspend fun getCountriesList(): List<Country>

    suspend fun getCountryById(id: Int): Country?

    suspend fun getCountryByIso(iso: String): Country?

    suspend fun getCurrencies(): CurrenciesStatus

    suspend fun getCountryInfo(countryId: Int): ApiStatus<Country>

    /**
     * Pushes the new device language to the server and refreshes the cached user.
     */
    suspend fun updateAppLanguage(language: String): ApiStatus<Boolean>

    suspend fun setLanguage(language: String)

    suspend fun getLanguage(): String

    val languageFlow: Flow<String>

    suspend fun updateDevice(language: String? = null): ApiStatus<Boolean>
}
