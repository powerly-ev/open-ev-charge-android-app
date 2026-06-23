package com.powerly.account.presentation.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.powerly.resources.R
import com.powerly.ui.dialogs.alert.AlertDialogProperties
import com.powerly.ui.dialogs.alert.MyAlertDialog
import com.powerly.ui.dialogs.alert.rememberAlertDialogState
import com.powerly.ui.dialogs.countries.CountriesDialog
import com.powerly.ui.dialogs.rememberMyDialogState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private const val TAG = "ProfileScreen"


@Composable
internal fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    navigateToWelcomeScreen: () -> Unit,
    focusPassword: Boolean = false,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val screenState = remember { viewModel.screenState }
    val countriesDialog = rememberMyDialogState()
    val deleteAccountDialog = rememberAlertDialogState()
    val signOutDialog = rememberAlertDialogState()

    val country by remember { viewModel.userCountry }
    val user by viewModel.userFlow.collectAsState()


    CountriesDialog(
        state = countriesDialog,
        countriesList = { viewModel.countries },
        selectedCountry = viewModel::getUserCountry,
        onSelectCountry = viewModel::updateUserCountry
    )

    MyAlertDialog(
        state = signOutDialog,
        title = stringResource(R.string.logout_title),
        message = stringResource(R.string.logout_confirmation),
        positiveButton = stringResource(R.string.yes),
        negativeButton = stringResource(R.string.no),
        positiveButtonClick = {
            coroutineScope.launch {
                val loggedOut = viewModel.logout()
                if (loggedOut) navigateToWelcomeScreen()
            }
        }
    )

    MyAlertDialog(
        state = deleteAccountDialog,
        message = """
                    ${context.getString(R.string.profile_confirm_delete_account)}
                    ${context.getString(R.string.profile_delete_account_description)}
                    """.trimIndent(),
        positiveButton = stringResource(R.string.yes),
        negativeButton = stringResource(R.string.no),
        positiveButtonClick = {
            coroutineScope.launch {
                val deleted = viewModel.deleteAccount()
                if (deleted) navigateToWelcomeScreen()
            }
        },
        properties = AlertDialogProperties(
            type = AlertDialogProperties.MODAL_DIALOG,
            messageJustify = true
        )
    )

    if (user != null) ProfileScreenContent(
        screenState = screenState,
        user = user!!,
        country = { country.name },
        focusPassword = focusPassword,
        currenciesFlow = viewModel.currencies,
        uiEvents = {
            when (it) {
                is ProfileEvents.Close -> {
                    onClose()
                }

                is ProfileEvents.SelectCountry -> {
                    countriesDialog.show()
                }

                is ProfileEvents.Save -> {
                    viewModel.updateProfile(
                        newUser = it.newUser,
                        password = it.password
                    )
                }

                is ProfileEvents.SelectCurrency -> {
                    viewModel.updateProfile(
                        newUser = user,
                        currency = it.currency.iso
                    )
                }

                is ProfileEvents.SignOut -> signOutDialog.show()

                is ProfileEvents.DeleteAccount -> deleteAccountDialog.show()
            }
        }
    )
}
