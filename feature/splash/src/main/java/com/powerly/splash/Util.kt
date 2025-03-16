package com.powerly.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.powerly.lib.CONSTANTS

private const val TAG = "SplashScreen"

/**
 * Reads app deep link data from the activity Intent.
 *
 * This function checks if the Intent has an action of `Intent.ACTION_VIEW` and
 * extracts the `POWER_SOURCE_ID` query parameter from the Intent's data URI.
 * If a valid ID is found, it is returned in a Bundle.
 *
 * @return A Bundle containing the `POWER_SOURCE_ID` if found, otherwise null.
 */
internal fun Intent.readAppLinks(): Bundle? {
    if (action == Intent.ACTION_VIEW) {
        val id = data?.getQueryParameter(CONSTANTS.POWER_SOURCE_ID)
        if (id != null) {
            return Bundle().apply {
                Log.i(TAG, "chargingPoint - $id")
                putString(CONSTANTS.POWER_SOURCE_ID, id)
            }
        }
    }
    return null
}