package com.powerly.user.welcome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import org.koin.androidx.compose.koinViewModel
import com.powerly.user.UserViewModel
import com.powerly.user.welcome.language.LanguagesDialog
import com.powerly.user.welcome.language.LanguagesViewModel
import com.powerly.ui.dialogs.rememberMyDialogState
import com.powerly.ui.dialogs.signIn.SignInOptions
import com.powerly.ui.dialogs.signIn.SignInOptionsDialog

private const val TAG = "WelcomeScreen"

/**
 * Welcome screen composable.
 *
 * This composable displays the welcome screen of the app. It allows users to
 * select their preferred language, sign in, or continue as a guest.
 *
 * @param navigateToLogin A callback function to navigate to several login methods.
 *
 * @see WelcomeScreenContent
 * @see LanguagesDialog
 * @see SignInOptionsDialog
 */

@Composable
internal fun WelcomeScreen(
    viewModel: UserViewModel,
    languagesViewModel: LanguagesViewModel = koinViewModel(),
    changeAppLanguage: () -> Unit,
    openUserAgreement: (Int) -> Unit,
    navigateToLogin: (SignInOptions) -> Unit,
    navigateToHome: () -> Unit
) {
    val signInOptionsDialog = rememberMyDialogState()
    val appVersion = remember { viewModel.appVersion }
    val languageName = remember { languagesViewModel.languageName }

    LaunchedEffect(Unit) {
        viewModel.detectCountry()
        viewModel.initRegistrationReminders()
    }

    /**
     * Define a callback function to handle UI events.
     */
    val signInEvents: (SignInOptions) -> Unit = {
        when (it) {
            is SignInOptions.Guest -> navigateToHome()
            else -> navigateToLogin(it)
        }
    }


    /**
     * Sign-in options bottom sheet dialog
     */
    SignInOptionsDialog(
        state = signInOptionsDialog,
        showGuest = true,
        uiEvents = signInEvents
    )

    /**
     * Welcome Screen Content
     */
    WelcomeScreenContent(
        appVersion = appVersion,
        selectedLanguage = languageName,
        onShowLanguageDialog = changeAppLanguage,
        onShowOptionsDialog = { signInOptionsDialog.show() },
        onOpenUserAgreement = openUserAgreement,
        signInEvents = signInEvents
    )
}
