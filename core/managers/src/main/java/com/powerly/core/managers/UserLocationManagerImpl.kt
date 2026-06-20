package com.powerly.core.managers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.powerly.core.domain.manager.UserLocationManager
import com.powerly.core.domain.model.location.Target
import org.koin.core.annotation.Single
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Default [UserLocationManager]: checks permission/service state and fetches the
 * physical location via [UserLocationProvider] (FusedLocation).
 */
@Single(binds = [UserLocationManager::class])
internal class UserLocationManagerImpl(
    private val context: Context,
    private val locationProvider: UserLocationProvider
) : UserLocationManager {

    private var lastLocation: Target? = null

    override fun getLastLocation() = lastLocation

    override val allEnabled: Boolean get() = permissionGranted && serviceEnabled

    override val serviceEnabled: Boolean get() = isLocationEnabled(context)

    override val permissionGranted: Boolean get() = isPermissionGranted(context)

    override suspend fun requestLocation(
        forceUpdate: Boolean
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


    companion object {
        private const val TAG = "LOCATION_MANAGER"

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
