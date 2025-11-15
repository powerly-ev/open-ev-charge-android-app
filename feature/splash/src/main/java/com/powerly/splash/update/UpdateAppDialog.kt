package com.powerly.splash.update

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import com.powerly.resources.R
import com.powerly.splash.SplashViewModel
import com.powerly.ui.dialogs.MyScreenBottomSheet
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun UpdateAppDialog(
    viewModel: SplashViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uriHandler= LocalUriHandler.current
    // Remember the app version string
    val appVersion = remember {
        String.format(
            "%s %s",
            context.getString(R.string.app_name),
            viewModel.appVersion
        )
    }

    BackHandler { }

    /**
     * Function to open the app link in app store
     */
    fun updateApp() {
        val appLink = viewModel.appLink
        uriHandler.openUri(appLink)
    }


    MyScreenBottomSheet(
        dismissOnBackPress = false,
        onDismiss = { }
    ) {
        UpdateAppScreenContent(
            appVersion = appVersion,
            onUpdate = ::updateApp
        )
    }
}
