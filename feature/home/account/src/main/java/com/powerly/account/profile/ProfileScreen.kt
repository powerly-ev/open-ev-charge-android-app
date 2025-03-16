package com.powerly.account.profile

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.powerly.resources.R
import com.powerly.ui.dialogs.alert.AlertDialogProperties
import com.powerly.ui.dialogs.alert.MyAlertDialog
import com.powerly.ui.dialogs.alert.rememberAlertDialogState
import kotlinx.coroutines.launch

private const val TAG = "ProfileScreen"


/**
 * Displays the user's profile screen, allowing them to view and edit their information,
 * select their country and currency, sign out, and delete their account.
 *
 * @param viewModel The [ProfileViewModel] instance that manages the profile screen's data and logic.
 * @param navigateToWelcomeScreen A lambda function to navigate to the welcome screen. Called after successful logout or account deletion.
 * @param selectedCountry A lambda function to handle the country selection event.
 * @param onClose A lambda function to handle the close button event.
 */
@Composable
internal fun ProfileScreen(
    viewModel: ProfileViewModel,
    navigateToWelcomeScreen: () -> Unit,
    focusPassword: Boolean = false,
    selectedCountry: () -> Unit,
    onClose: (updateProfile: Boolean) -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val screenState = remember { viewModel.screenState }
    val deleteAccountDialog = rememberAlertDialogState()
    val signOutDialog = rememberAlertDialogState()

    var country by remember { viewModel.userCountry }
    var user by remember { viewModel.user }

    LaunchedEffect(Unit) {
        viewModel.initCountry()
    }


    BackHandler(enabled = true) {
        onClose(viewModel.isProfileUpdated())
    }

    /**
     * Displays a dialog for the user to logout his account.
     */

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

    /**
     * Displays a dialog for the user to delete his account.
     */

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

    // Display the Profile screen content and handle UI events
    if (user != null) ProfileScreenContent(
        screenState = screenState,
        user = user!!,
        country = { country.name },
        focusPassword = focusPassword,
        currenciesFlow = viewModel.currencies,
        uiEvents = {
            when (it) {
                is ProfileEvents.Close -> {
                    onClose(viewModel.isProfileUpdated())
                }

                is ProfileEvents.SelectCountry -> selectedCountry()
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
