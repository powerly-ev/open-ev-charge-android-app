package com.powerly.user.presentation.welcome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.powerly.resources.R
import com.powerly.ui.dialogs.rememberMyDialogState
import com.powerly.ui.dialogs.signIn.SignInOptions
import com.powerly.ui.dialogs.signIn.SignInOptionsDialog
import com.powerly.user.presentation.UserViewModel
import com.powerly.user.presentation.welcome.language.LanguagesDialog
import com.powerly.user.presentation.welcome.language.LanguagesViewModel
import org.koin.androidx.compose.koinViewModel

private const val TAG = "WelcomeScreen"

@Composable
internal fun WelcomeScreen(
    viewModel: UserViewModel,
    languagesViewModel: LanguagesViewModel = koinViewModel(),
    changeAppLanguage: () -> Unit,
    openUserAgreement: (Int) -> Unit,
    navigateToLogin: (SignInOptions) -> Unit,
    navigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val signInOptionsDialog = rememberMyDialogState()
    val appVersion = remember { viewModel.appVersion }
    var languageName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.detectCountry()
        viewModel.initRegistrationReminders()
    }

    LaunchedEffect(Unit) {
        languagesViewModel.language.collect { lang ->
            val localeMap: HashMap<String, String> = HashMap()
            val names = context.resources.getStringArray(R.array.available_languages)
            val codes = context.resources.getStringArray(R.array.available_language_codes)
            for (i in names.indices) localeMap[codes[i]] = names[i]
            languageName = localeMap[lang].orEmpty()
        }
    }

    val signInEvents: (SignInOptions) -> Unit = {
        when (it) {
            is SignInOptions.Guest -> navigateToHome()
            else -> navigateToLogin(it)
        }
    }

    SignInOptionsDialog(
        state = signInOptionsDialog,
        showGuest = true,
        uiEvents = signInEvents
    )

    WelcomeScreenContent(
        appVersion = appVersion,
        selectedLanguage = languageName,
        onShowLanguageDialog = changeAppLanguage,
        onShowOptionsDialog = { signInOptionsDialog.show() },
        onOpenUserAgreement = openUserAgreement,
        signInEvents = signInEvents
    )
}
