package com.powerly.map.home

import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.ViewModel
import com.powerly.core.data.model.SourceStatus
import com.powerly.core.data.repositories.PowerSourceRepository
import com.powerly.core.model.powerly.PowerSource
import com.powerly.lib.CONSTANTS.POWER_SOURCE_ID
import com.powerly.core.network.DeviceHelper
import com.powerly.lib.managers.StorageManager
import com.powerly.lib.managers.UserLocationManager
import com.powerly.ui.util.ActivityResultState
import com.powerly.ui.util.PermissionsState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val powerSourceRepository: PowerSourceRepository,
    private val locationManager: UserLocationManager,
    private val deviceHelper: DeviceHelper,
    private val storageManager: StorageManager
) : ViewModel() {

    val isLoggedIn: Boolean get() = storageManager.isLoggedIn

    fun requestLocationServices(
        permissionsState: PermissionsState,
        activityResult: ActivityResultState,
        requestManually: Boolean = false,
        onAllowed: () -> Unit
    ) {
        Log.i(TAG, "requestLocationServices")
        // Location service and permissions are enabled, fetch location
        if (locationManager.allEnabled) onAllowed()
        // Location service is disabled or  permissions are not granted
        else {
            if (locationManager.serviceEnabled.not() && requestManually) {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                activityResult.startActivityForResult(intent) {
                    if (locationManager.serviceEnabled) {
                        if (permissionsState.isGranted().not()) {
                            permissionsState.request { granted ->
                                if (granted) onAllowed()
                                else permissionsState.requestManually { mGranted ->
                                    if (mGranted) onAllowed()
                                }
                            }
                        } else onAllowed()
                    }
                }
            } else if (permissionsState.isGranted().not()) {
                permissionsState.request { granted ->
                    if (granted) onAllowed()
                    else if (requestManually) permissionsState.requestManually { mGranted ->
                        if (mGranted) onAllowed()
                    }
                }
            }
        }
    }

    suspend fun getPowerSourceFromDeepLink(intent: Intent): PowerSource? {
        return if (intent.hasExtra(POWER_SOURCE_ID)) {
            val identifier = intent.getStringExtra(POWER_SOURCE_ID).orEmpty()
            Log.i(TAG, "deep-link-identifier - $identifier")
            intent.removeExtra(POWER_SOURCE_ID)
            val it = powerSourceRepository.getPowerSourceByIdentifier(identifier)
            if (it is SourceStatus.Success) it.powerSource
            else null
        } else null
    }

    fun getSupportNumber(): String? = deviceHelper.supportNumber

    companion object {
        private const val TAG = "HomeViewModel"
    }
}