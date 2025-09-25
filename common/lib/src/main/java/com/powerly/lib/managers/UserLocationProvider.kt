package com.powerly.lib.managers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.core.net.toUri
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import org.koin.core.annotation.Single

@Single
class UserLocationProvider(
    private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)


    @SuppressLint("MissingPermission")
    internal fun requestPhysicalLocation(
        onFetchLocation: (latitude: Double, longitude: Double) -> Unit,
        onError: (String) -> Unit
    ) {
        Log.i(TAG, "requestPhysicalLocation")
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) onFetchLocation(
                location.latitude,
                location.longitude
            ) else onError("")
        }
    }

    @SuppressLint("MissingPermission")
    internal fun requestUpdatedPhysicalLocation(
        onFetchLocation: (latitude: Double, longitude: Double) -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        Log.i(TAG, "requestUpdatedPhysicalLocation")
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false

            }).addOnSuccessListener { location: Location? ->
            Log.i(TAG, "OnSuccessListener")
            if (location != null) onFetchLocation(
                location.latitude,
                location.longitude
            ) else onError?.invoke("location is null")
        }.addOnFailureListener {
            Log.i(TAG, "OnFailureListener")
            onError?.invoke("FailureListener\n" + it.message.orEmpty())
        }.addOnCanceledListener {
            Log.i(TAG, "OnCanceledListener")
            onError?.invoke("CanceledListener")
        }

    }

    internal fun navigateInMap(latitude: Double, longitude: Double) {
        val directions = "http://maps.google.com/maps?saddr=&" +
                "daddr=$latitude,$longitude&" +
                "directionsmode=driving"
        val gMapPackage = "com.google.android.apps.maps"
        val intent = Intent(Intent.ACTION_VIEW, directions.toUri())
        intent.setPackage(gMapPackage)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "LOCATION_MANAGER"
    }
}