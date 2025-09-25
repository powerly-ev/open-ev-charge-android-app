package com.powerly.lib.managers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.powerly.core.model.location.Target
import org.koin.core.annotation.Single
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Manages user location-related functionality such as checking location service status,
 * requesting location permissions, fetching user's physical location, and map navigation.
 *
 * @property context The Context from which this manager is invoked.
 * @constructor Creates an instance of UserLocationManager.
 */
@Single
class UserLocationManager(
    private val context: Context,
    private val locationProvider: UserLocationProvider
) {

    private var lastLocation: Target? = null

    fun getLastLocation() = lastLocation

    /**
     * Checks if both location permissions and location services are enabled.
     */
    val allEnabled: Boolean get() = permissionGranted && serviceEnabled

    /**
     * Checks if location services are enabled.
     */
    val serviceEnabled: Boolean get() = isLocationEnabled(context)

    /**
     * Checks if location permissions are granted.
     */
    val permissionGranted: Boolean get() = isPermissionGranted(context)

    /**
     * Requests the user's physical location.
     *
     * This method prioritizes using the cached location for efficiency. If `forceUpdate` is set to
     * `true` or no cached location is available, it requests an updated location from the device.
     *
     * Location access requires appropriate permissions to be granted. If permissions are not granted,
     *
     * @param forceUpdate If `true`, forces an updated location fetch even if cached data is available.
     */
    suspend fun requestLocation(
        forceUpdate: Boolean = false
    ): Target? = suspendCoroutine { continuation ->
        if (isPermissionGranted(context)) {
            if (forceUpdate) {
                locationProvider.requestUpdatedPhysicalLocation(
                    onFetchLocation = { lat, lng ->
                        val target = Target(lat, lng)
                        lastLocation = target
                        continuation.resume(target)
                    },
                    onError = {
                        continuation.resume(null)
                    }
                )
            } else {
                locationProvider.requestPhysicalLocation(
                    onFetchLocation = { lat, lng ->
                        val target = Target(lat, lng)
                        lastLocation = target
                        continuation.resume(target)
                    },
                    onError = {
                        locationProvider.requestUpdatedPhysicalLocation(
                            onFetchLocation = { lat, lng ->
                                val target = Target(lat, lng)
                                lastLocation = target
                                continuation.resume(target)
                            },
                            onError = {
                                continuation.resume(null)
                            }
                        )
                    }
                )
            }
        } else {
            continuation.resume(null)
        }
    }

    /**
     * Opens the map application to navigate to the specified latitude and longitude.
     *
     * @param latitude The latitude of the destination.
     * @param longitude The longitude of the destination.
     */
    fun navigateToMap(latitude: Double, longitude: Double) {
        locationProvider.navigateInMap(latitude, longitude)
    }

    companion object {
        private const val TAG = "LOCATION_MANAGER"

        /**
         * Checks if location permissions are granted.
         *
         * @param context The Context in which to check permissions.
         * @return `true` if either fine or coarse location permission is granted, `false` otherwise.
         */
        fun isPermissionGranted(context: Context): Boolean {
            val preciseLocation = (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)

            val approxLocation = (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)

            return preciseLocation || approxLocation
        }

        /**
         * Checks if location services are enabled.
         *
         * @param context The Context in which to check location services.
         * @return `true` if either GPS or network location provider is enabled, `false` otherwise.
         */
        fun isLocationEnabled(context: Context): Boolean {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var gpsEnabled = false
            var networkEnabled = false

            try {
                gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            try {
                networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return gpsEnabled || networkEnabled
        }
    }
}