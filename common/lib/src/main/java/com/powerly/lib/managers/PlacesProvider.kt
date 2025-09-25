package com.powerly.lib.managers

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import com.powerly.core.model.location.MyAddress
import org.koin.core.annotation.Single
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Single
class PlacesProvider (
     private val context: Context
) {
    companion object {
        private const val TAG = "PlacesProvider"
    }

    /**
     * Detects the address for the given latitude and longitude coordinates.
     *
     * This function uses the `Geocoder` to reverse geocode the coordinates and obtain the address.
     * It handles different API levels to ensure compatibility and uses a callback to deliver the result.
     *
     * @param latitude The latitude coordinate.
     * @param longitude The longitude coordinate.
     * @param lang The language code for the address (e.g., "en"). Defaults to the device's default language.
     */
    @Suppress("DEPRECATION")
    internal suspend fun detectAddress(
        latitude: Double,
        longitude: Double,
        lang: String
    ): MyAddress? = suspendCoroutine { continuation ->
        try {
            val geocoder = Geocoder(context, Locale(lang))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(
                    latitude, longitude, 1,
                    object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            val myAddress = processAddresses(addresses)
                            continuation.resume(myAddress)
                        }

                        override fun onError(errorMessage: String?) {
                            continuation.resume(null)
                            errorMessage?.let { Log.e(TAG, "Error: $it") }
                        }
                    }
                )
            } else {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                val myAddress = processAddresses(addresses)
                continuation.resume(myAddress)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            continuation.resume(null)
        }
    }

    /**
     * Processes a list of addresses and extracts the relevant information to create a [MyAddress] object.
     *
     * If the provided list is not empty, it takes the first address and extracts the address line, city, state, and zip code.
     * Then, it creates a [MyAddress] object with this information and invokes the `onAddressChanged` callback with the created object.
     * If the list is empty, the callback is not invoked.
     *
     * @param addresses The list of addresses to process.
     */
    private fun processAddresses(addresses: List<Address>?): MyAddress? {
        return if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            MyAddress(
                addressLine1 = address.getAddressLine(0),
                city = address.locality,
                state = address.subAdminArea,
                zipcode = address.postalCode,
                countryCode = address.countryCode
            )
        } else null
    }

    internal fun initMap() {

    }
}