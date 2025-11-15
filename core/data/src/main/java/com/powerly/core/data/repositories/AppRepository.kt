package com.powerly.core.data.repositories

import com.powerly.core.data.model.CurrenciesStatus
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.location.Country
import kotlinx.coroutines.flow.Flow

interface AppRepository {

    /**
     * Determines whether the onboarding screen should be shown once.
     *
     * @return `true` if onboarding should be shown, `false` otherwise.
     */
    suspend fun showOnBoardingOnce(): Boolean

    /**
     * Determines whether the registration notification should be shown.
     * The notification should be shown only for unregistered users.
     *
     * @return `true` if the notification should be shown, `false` otherwise.
     */
    suspend fun showRegisterNotification(): Boolean


    /**
     * Retrieves a list of countries.
     *
     * @return  [ApiStatus] results containing a list of countries.
     */
    suspend fun updateCountries(): ApiStatus<List<Country>>

    /**
     * Retrieves the user's country.
     *
     * @return The user's country as a [Country] object.
     */
    suspend fun getUserCountry(): Country?

    /**
     * Retrieves a list of countries.
     *
     * @return A list of [Country] objects.
     */
    suspend fun getCountriesList(): List<Country>

    /**
     * Retrieves a country by its ID.
     *
     * @param id The ID of the country.
     */
    suspend fun getCountryById(id: Int): Country?

    /**
     * Retrieves a country by its ISO code.
     *
     * @param iso The ISO code of the country.
     */
    suspend fun getCountryByIso(iso: String): Country?

    /**
     * Retrieves a list of currencies.
     *
     * @return  [ApiStatus] results containing a list of currencies.
     */
    suspend fun getCurrencies(): CurrenciesStatus

    /**
     * Retrieves information about a specific country.
     *
     * @param countryId The ID of the country.
     * @return  [ApiStatus] results containing country information.
     */
    suspend fun getCountryInfo(countryId: Int): ApiStatus<Country>

    /**
     * Updates app language at the server of the current device.
     *
     * @param language The new device language.
     * @return  [ApiStatus] results indicating the success or failure of the update.
     */
    suspend fun updateAppLanguage(language: String): ApiStatus<Boolean>

    /**
     * Sets the current language.
     *
     * @param language The language to set.
     */
    suspend fun setLanguage(language: String)

    /**
     * Retrieves the current language.
     *
     * @return The current language as a [String].
     */
    suspend fun getLanguage(): String

    /**
     * A flow that emits the current language.
     */
    val languageFlow: Flow<String>

    /**
     * Updates device information at the server of the current device.
     *
     * @return  [ApiStatus] results indicating the success or failure of the update.
     */
    suspend fun updateDevice(language: String? = null): ApiStatus<Boolean>
}

