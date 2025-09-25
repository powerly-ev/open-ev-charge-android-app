package com.powerly.lib.managers

import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.telephony.TelephonyManager
import android.util.Log
import com.powerly.core.model.location.Country
import org.koin.core.annotation.Single

/**
 * Manages country detection and provides access to country information.
 *
 * This class provides methods to detect the user's country using either the TelephonyManager or
 * location information. It also stores a map of countries with their IDs and ISO codes.
 */
@Single
class CountryManager (
     private val context: Context,
    private val locationManager: UserLocationManager,
    private val placesManager: PlacesManager,
    private val storageManager: StorageManager
) {
    companion object {
        private const val TAG = "CountryManager"
        private var countries = HashMap<String, Country>()
        fun getCountryList() = countries.values.toList()
        private const val DEFAULT_COUNTRY_ISO = "JO" //jordan code two letters
    }

    /**
     * Detects the user's country using the TelephonyManager or location information.
     *
     * This method first attempts to detect the country using the TelephonyManager. If this fails,
     * it requests the user's location and uses a reverse geocoding API to determine the country.
     * If both methods fail, it returns the default country (UK).
     *
     */
    suspend fun detectActualCountry(): Country? {
        val phoneCountry = detectCountryByTelephonyManager()
        if (phoneCountry != null) {
            return phoneCountry
        } else {
            // request user location (latitude,longitude)
            val location = locationManager.requestLocation() ?: return defaultCountry
            // detect geo address by (latitude,longitude) to get country code
            val myAddress = placesManager.detectAddress(location.latitude, location.longitude)
            val countryCode = myAddress?.countryCode.orEmpty()
            // search for country by code
            val country = getCountryByCode(iso = countryCode) ?: defaultCountry
            return country
        }
    }

    /**
     * Detects the user's country using the TelephonyManager.
     *
     * If the TelephonyManager cannot determine the country, it returns the default country (UK).
     *
     * @param context The Context used for accessing the TelephonyManager.
     * @return The detected Country or the default country if detection fails.
     */
    fun detectCountry(): Country? {
        return detectCountryByTelephonyManager()
            ?: getCountryById(storageManager.countryId)
            ?: defaultCountry
    }

    fun getSavedCountry(): Country? {
        val country = getCountryById(storageManager.countryId) ?: detectCountry()
        Log.v(TAG, "getSavedCountry - ${country?.name}")
        return country
    }

    private val defaultCountry: Country? get() = countries[DEFAULT_COUNTRY_ISO]

    /**
     * Detects the user's country using the TelephonyManager.
     *
     * This method retrieves the network country ISO code from the TelephonyManager and matches it
     * against the list of countries.
     *
     * @param context The Context used for accessing the TelephonyManager.
     * @return The detected Country or null if detection fails.
     */
    private fun detectCountryByTelephonyManager(): Country? {
        return try {
            val tm = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            val countryIso = tm.networkCountryIso
            Log.v(TAG, "countryIso - $countryIso")
            for (country in countries.values) {
                if (country.iso.equals(countryIso, ignoreCase = true)) {
                    return country
                }
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getCountryById(id: Int?): Country? = countries["$id"]

    private fun getCountryByCode(iso: String): Country? =
        countries.values.firstOrNull { it.iso.equals(iso, ignoreCase = true) }

    fun initCountries(list: List<Country>) {
        countries.clear()
        list.forEach { country ->
            val id = country.id
            countries[id.toString()] = country
        }
    }
}