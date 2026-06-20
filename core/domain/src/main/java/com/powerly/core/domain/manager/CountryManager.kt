package com.powerly.core.domain.manager

import com.powerly.core.domain.model.location.Country

/**
 * Detects and resolves the user's country.
 *
 * Implementations decide the strategy (e.g. telephony, then location + reverse
 * geocoding, then a default); callers depend only on this domain abstraction.
 */
interface CountryManager {

    /** Detects the current country via device signals (telephony/location), falling back to a default. */
    suspend fun detectActualCountry(): Country?

    /** Resolves the country from device signals or the stored user country, falling back to a default. */
    suspend fun detectCountry(): Country?

    /** Returns the saved user country, detecting it if not yet stored. */
    suspend fun getSavedCountry(): Country?
}
