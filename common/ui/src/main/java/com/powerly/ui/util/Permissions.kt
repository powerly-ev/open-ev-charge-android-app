package com.powerly.ui.util

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.powerly.core.data.model.PermissionsState
import kotlin.collections.get


@Composable
private fun RequestPermissionsTest() {
    val permissionsState = rememberPermissionsState(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(Unit) {
        permissionsState.request {

        }
        permissionsState.requestManually {

        }
    }
}

/**
 * The `rememberPermissionsState` function is a composable function that creates and remembers a [PermissionsState] object.
 * The [PermissionsState] object provides functions to request permissions, check if permissions are granted, and launch the app settings screen to manually grant permissions.
 *
 * @param permissions The permissions to request. This is a vararg parameter, which means you can pass in multiple permissions.
 * @return A [PermissionsState] object that can be used to request and check permissions.
 */
@Composable
fun rememberPermissionsState(vararg permissions: String): PermissionsState {
    // Get the current context.
    val context = LocalContext.current
    // Convert the vararg permissions to an array.
    val permissionsList = arrayOf(*permissions)
    var singlePermission by remember { mutableStateOf<String?>(null) }
    // Store the result callback for the permission request.
    var onResult by remember { mutableStateOf<((Boolean) -> Unit)?>(null) }
    // Store the result callback for the manual permission request.
    var onManualResult by remember { mutableStateOf<((Boolean) -> Unit)?>(null) }

    /**
     * Checks if all the permissions are granted.
     * This function iterates through all the permissions and checks if each one is granted.
     *
     * @return True if all the permissions are granted, false otherwise.
     */
    fun permissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all {
            ActivityCompat.checkSelfPermission(context, it) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    // Create a launcher for the permission request.
    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result: Map<String, Boolean> ->
        // Check if the permissions were granted.
        var granted = false
        if (singlePermission != null) {
            listOf(singlePermission).forEach { granted = granted || result[it] ?: false }
            singlePermission = null
        } else {
            permissionsList.forEach { granted = granted || result[it] ?: false }
        }
        // Invoke the result callback.
        onResult?.invoke(granted)
    }

    // Create a launcher for the manual permission request.
    val activityResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // Invoke the result callback with the current permission status.
        onManualResult?.invoke(permissionsGranted(permissionsList))
    }

    // Create and remember the PermissionsState object.
    return remember {
        PermissionsState(
            // Request permissions.
            request = { callback ->
                // Store the result callback.
                onResult = callback
                // Launch the permission request.
                permissionsLauncher.launch(permissionsList)
            },
            requestSpecific = { permission, callback ->
                // Store the result callback.
                singlePermission = permission
                onResult = callback
                // Launch the permission request.
                permissionsLauncher.launch(arrayOf(permission))
            },
            // Request permissions manually.
            requestManually = { result ->
                // Store the result callback.
                onManualResult = result
                // Create an intent to launch the app settings screen.
                val uri = Uri.fromParts("package", context.packageName, null)
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    this.data = uri
                }
                // Launch the intent.
                activityResultLauncher.launch(intent)
            },
            // Check if permissions are granted.
            isGranted = {
                permissionsGranted(permissionsList)
            },
            isItGranted = { permission ->
                permissionsGranted(arrayOf(permission))
            }
        )
    }
}
