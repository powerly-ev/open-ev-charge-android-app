package com.powerly.core.domain.manager

import com.powerly.core.domain.model.location.Target

/**
 * Access to the device's location state and last/current physical location.
 *
 * Permission/service checks and the platform location provider are implementation
 * details; callers depend only on this abstraction.
 */
interface UserLocationManager {

    /** True when both location permission is granted and location services are on. */
    val allEnabled: Boolean

    /** True when device location services are enabled. */
    val serviceEnabled: Boolean

    /** True when a location permission is granted. */
    val permissionGranted: Boolean

    /** The last location resolved during this session, if any. */
    fun getLastLocation(): Target?

    /**
     * Resolves the user's location, preferring the cached value unless [forceUpdate] is set.
     * Returns null if permission is missing or the location can't be obtained.
     */
    suspend fun requestLocation(forceUpdate: Boolean = false): Target?
}
