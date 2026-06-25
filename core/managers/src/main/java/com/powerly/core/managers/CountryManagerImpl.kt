package com.powerly.core.managers

import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.telephony.TelephonyManager
import android.util.Log
import com.powerly.core.domain.manager.CountryManager
import com.powerly.core.domain.manager.UserLocationManager
import com.powerly.core.domain.repository.AppRepository
import com.powerly.core.domain.model.location.Country
import org.koin.core.annotation.Single

/**
 * Default [CountryManager]: detects the country via the TelephonyManager, then falls
 * back to the user's location (reverse-geocoded) and finally a default country.
 */
@Single(binds = [CountryManager::class])
internal class CountryManagerImpl(
    private val context: Context,
    private val appRepository: AppRepository,
    private val locationManager: UserLocationManager,
    private val placesManager: PlacesManager,
) : CountryManager {

    override suspend fun detectActualCountry(): Country? {
        val phoneCountry = detectCountryByTelephonyManager()
        if (phoneCountry != null) {
            return phoneCountry
        } else {
            // request user location (latitude,longitude)
            val location = locationManager.requestLocation() ?: return getDefaultCountry()
            // detect geo address by (latitude,longitude) to get country code
            val myAddress = placesManager.detectAddress(location.latitude, location.longitude)
            val countryCode = myAddress?.countryCode.orEmpty()
            // search for country by code
            val country = appRepository.getCountryByIso(iso = countryCode) ?: getDefaultCountry()
            return country
        }
    }

    override suspend fun detectCountry(): Country? {
        return detectCountryByTelephonyManager()
            ?: appRepository.getUserCountry()
            ?: getDefaultCountry()
    }

    override suspend fun getSavedCountry(): Country? {
        val country = appRepository.getUserCountry() ?: detectCountry()
        Log.v(TAG, "getSavedCountry - ${country?.name}")
        return country
    }

    private suspend fun getDefaultCountry(): Country? {
        return appRepository.getCountryByIso(DEFAULT_COUNTRY_ISO)
    }

    private suspend fun detectCountryByTelephonyManager(): Country? {
        return try {
            val tm = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            val countryIso = tm.networkCountryIso.uppercase()
            Log.v(TAG, "countryIso - $countryIso")
            return appRepository.getCountryByIso(countryIso)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        private const val TAG = "CountryManager"
        private const val DEFAULT_COUNTRY_ISO = "JO" //jordan code two letters
    }
}
