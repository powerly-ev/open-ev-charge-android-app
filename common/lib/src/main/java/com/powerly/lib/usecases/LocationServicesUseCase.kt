package com.powerly.lib.usecases

import android.content.Intent
import android.provider.Settings
import android.util.Log
import com.powerly.core.data.model.ActivityResultState
import com.powerly.core.data.model.PermissionsState
import com.powerly.lib.managers.UserLocationManager
import org.koin.core.annotation.Single

@Single
class LocationServicesUseCase(
    private val locationManager: UserLocationManager
) {
    operator fun invoke(
        permissionsState: PermissionsState,
        activityResult: ActivityResultState,
        requestManually: Boolean = false,
        onAllowed: () -> Unit
    ) {
        Log.i(TAG, "Requesting location services")
        // Location service and permissions are enabled, fetch location
        if (locationManager.allEnabled) {
            onAllowed()
        }
        // Location service is disabled or permissions are not granted
        else {
            if (!locationManager.serviceEnabled && requestManually) {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                activityResult.startActivityForResult(intent) {
                    if (locationManager.serviceEnabled) {
                        if (!permissionsState.isGranted()) {
                            permissionsState.request { granted ->
                                if (granted) {
                                    onAllowed()
                                } else {
                                    permissionsState.requestManually { mGranted ->
                                        if (mGranted) onAllowed()
                                    }
                                }
                            }
                        } else {
                            onAllowed()
                        }
                    }
                }
            } else if (!permissionsState.isGranted()) {
                permissionsState.request { granted ->
                    if (granted && locationManager.serviceEnabled) {
                        onAllowed()
                    } else if (requestManually) {
                        permissionsState.requestManually { mGranted ->
                            if (mGranted) onAllowed()
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "RequestLocationServicesUseCase"
    }
}
