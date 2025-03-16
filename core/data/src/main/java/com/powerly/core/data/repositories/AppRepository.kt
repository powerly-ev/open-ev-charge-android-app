package com.powerly.core.data.repositories

import com.powerly.core.data.model.CurrenciesStatus
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.user.DeviceBody
import com.powerly.core.model.location.Country

interface AppRepository {

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
     * Updates app information.
     *
     * @param request The [DeviceBody] containing the updated information.
     * @return  [ApiStatus] results indicating the success or failure of the update.
     */
    suspend fun updateDevice(request: DeviceBody): ApiStatus<Boolean>
}

