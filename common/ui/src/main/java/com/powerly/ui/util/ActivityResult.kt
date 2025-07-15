package com.powerly.ui.util

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.powerly.core.data.model.ActivityResultState

/**
 * Creates and remembers an [ActivityResultState] object.
 *
 * This composable function provides a convenient way to handle activity results
 * in Jetpack Compose. It uses the `remember` function to store the state
 * of the `ActivityResultState` object across recompositions, and the
 * `rememberLauncherForActivityResult` function to create an activity result
 * launcher.
 *
 * The `ActivityResultState` object provides two functions for launching
 * activities: `startActivity` and `startActivityForResult`. Both functions take
 * a callback function as an argument, which will be invoked when the activity
 * finishes. The callback function receives the activity result and a boolean
 * value indicating whether the result is OK.
 *
 * The `startActivity` function takes the package name of the activity to
 * launch, an optional bundle of extras, and a callback function. The
 * `startActivityForResult` function takes an intent and a callback function.
 *
 * Example usage:
 * ```
 * val resultState = rememberActivityResultState( )
 *
 * Button(onClick = {
 * resultState.startActivity(
 *     packageName = "com.example.myapp",
 *     bundle = bundleOf("key" to "value")
 * ) { result, isOk ->
 *     // Handle the activity result
 * }
 * }) {
 * Text("Start Activity")
 * }
 *```
 * @return An [ActivityResultState] object.
 **/

@Composable
fun rememberActivityResultState(): ActivityResultState {
    // Get the current context.
    val context = LocalContext.current
    var onResult by remember { mutableStateOf<((ActivityResult?) -> Unit)?>(null) }

    val activityResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        onResult?.invoke(result)
    }

    // Create and remember the PermissionsState object.
    return remember {
        ActivityResultState(
            startActivity = { packageName, callback ->
                onResult = callback
                val intent = Intent(context, Class.forName(packageName))
                activityResultLauncher.launch(intent)
            },
            startActivityWithBundle = { packageName, bundle, callback ->
                onResult = callback
                val intent = Intent(context, Class.forName(packageName))
                bundle?.let { intent.putExtras(bundle) }
                activityResultLauncher.launch(intent)
            },
            startActivityForResult = { intent, callback ->
                onResult = callback
                activityResultLauncher.launch(intent)
            }
        )
    }
}


val ActivityResult?.isOk: Boolean get() = this?.resultCode == RESULT_OK
