package com.powerly.core.data.repositories

import com.powerly.core.data.model.CurrenciesStatus
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.location.Country

interface AppRepository {

    /**
     * Determines whether the onboarding screen should be shown once.
     *
     * @return `true` if onboarding should be shown, `false` otherwise.
     */
    fun showOnBoardingOnce(): Boolean

    /**
     * Determines whether the registration notification should be shown.
     * The notification should be shown only for unregistered users.
     *
     * @return `true` if the notification should be shown, `false` otherwise.
     */
    fun showRegisterNotification(): Boolean

    /**
     * Retrieves the current language of the app.
     *
     * @return A string representing the current language code.
     */
    fun getLanguage(): String

    /**
     * Retrieves the currently selected currency.
     *
     * @return The currency code as a [String].
     */
    fun getCurrency(): String

    /**
     * Retrieves a list of countries.
     *
     * @return  [ApiStatus] results containing a list of countries.
     */
    suspend fun getCountries(): ApiStatus<List<Country>>

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
     * Updates device information at the server of the current device.
     *
     * @return  [ApiStatus] results indicating the success or failure of the update.
     */
    suspend fun updateDevice(language: String? = null): ApiStatus<Boolean>
}

