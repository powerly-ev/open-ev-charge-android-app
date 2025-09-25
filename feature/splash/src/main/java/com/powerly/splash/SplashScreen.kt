package com.powerly.splash

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.koinViewModel
import com.powerly.resources.R
import com.powerly.ui.dialogs.alert.AlertDialogProperties
import com.powerly.ui.dialogs.alert.MyAlertDialog
import com.powerly.ui.dialogs.alert.rememberAlertDialogState
import com.powerly.ui.dialogs.loading.LoadingDialog
import com.powerly.ui.dialogs.loading.rememberLoadingState
import com.powerly.ui.extensions.intent

private const val TAG = "SplashScreen"

/**
 * Displays the splash screen and handles initial app loading and navigation.
 *
 * This composable performs the following:
 * - Checks internet connectivity.
 * - Loads country data if online.
 * - Authenticates the user if logged in.
 * - Navigates to the appropriate screen (home or welcome).
 * - Handles errors (maintenance, no internet, app update).
 * - Presents the splash content with an animation.
 *
 * @param viewModel The [SplashViewModel] for network and authentication.
 * @param openHomeScreen Callback to navigate to the home screen.
 * @param openWelcomeScreen Callback to navigate to the welcome screen.
 * @param openAppUpdate Callback to navigate to the app update screen.
 * @param onClose Callback to close the app.
 */
@Composable
internal fun SplashScreen(
    viewModel: SplashViewModel = koinViewModel(),
    openHomeScreen: (extras: Bundle?) -> Unit,
    openWelcomeScreen: () -> Unit,
    openAppUpdate: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val loadingState = rememberLoadingState()
    val errorDialog = rememberAlertDialogState()
    val maintenanceDialog = rememberAlertDialogState()
    var retry by remember { mutableStateOf(false) }
    var splashAction by remember { mutableStateOf<SplashAction?>(null) }
    var splashAnimationDone by remember { mutableStateOf(false) }

    /**
     * Handles splash screen actions based on the provided action and animation state.
     *
     * This function updates the state of the splash screen based on the given action
     * and animation completion status. It then performs the corresponding action,
     * such as showing a maintenance message, displaying a no internet message,
     * or navigating to different screens based on the action.
     *
     * - Note: Splash action can only performed when there an action such (OpenWelcomeScreen or
     * OpenHomeScreen or show error message)
     * splashAction!=null && and when splash animation is completed splashAnimationDone = true
     *
     * @param action The [SplashAction] to perform.
     * @param animationDone Indicates whether the splash screen animation is complete.
     */
    fun onSplashScreenAction(
        action: SplashAction? = null,
        animationDone: Boolean? = null
    ) {
        action?.let { splashAction = action }
        animationDone?.let { splashAnimationDone = true }
        Log.v(TAG, "---- Action - $splashAction | animation - $splashAnimationDone")

        if (splashAnimationDone.not()) return

        when (val it = splashAction) {
            SplashAction.Maintenance -> maintenanceDialog.show()

            is SplashAction.TryAgain -> {
                errorDialog.show(it.message)
            }

            SplashAction.NoInternet -> {
                val message = context.getString(R.string.internet_connection_error)
                errorDialog.show(message)
            }

            SplashAction.OpenHomeScreen -> {
                val intent = context.intent
                // read deep link data if exists
                val extras = intent.readAppLinks()
                openHomeScreen(extras)
            }

            SplashAction.OpenWelcomeScreen -> openWelcomeScreen()
            SplashAction.UpdateApp -> openAppUpdate()

            null -> {}
        }
        splashAction = null
    }


    /**
     * A LaunchedEffect that checks for internet connection on launch.
     *
     * This effect triggers when the `retry` state changes. It checks for internet
     * connectivity and performs the following actions:
     * - If online, it calls `loadCountries` to fetch the list of countries.
     * - If offline, it triggers the `NoInternet` splash action to display an error message.
     */
    LaunchedEffect(retry) {
        Log.i(TAG, "checkForInternetConnection")
        if (viewModel.isOnline()) {
            if (splashAnimationDone) loadingState.show()
            val action = viewModel.loadCountries()
            onSplashScreenAction(action)
        } else {
            onSplashScreenAction(SplashAction.NoInternet)
        }
    }

    LoadingDialog(loadingState)

    MyAlertDialog(
        state = maintenanceDialog,
        icon = R.drawable.ic_alert,
        message = stringResource(R.string.maintenance_mode_error),
        positiveButton = stringResource(R.string.retry),
        negativeButton = stringResource(R.string.no),
        positiveButtonClick = { retry = retry.not() },
        properties = AlertDialogProperties(canDismiss = false)
    )

    MyAlertDialog(
        state = errorDialog,
        icon = R.drawable.ic_alert,
        positiveButton = stringResource(R.string.retry),
        negativeButton = stringResource(R.string.dialog_cancel),
        positiveButtonClick = { retry = retry.not() },
        negativeButtonClick = onClose,
        properties = AlertDialogProperties(
            canDismiss = false,
            messageJustify = true
        )
    )

    // splash screen content
    SplashScreenContent(
        onLogoLoaded = {
            if (splashAnimationDone.not()) {
                onSplashScreenAction(animationDone = true)
            }
        }
    )
}


