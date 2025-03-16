package com.powerly.user.email.password.enter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.powerly.user.email.EmailLoginViewModel
import com.powerly.resources.R
import com.powerly.ui.dialogs.alert.AlertDialogProperties
import com.powerly.ui.dialogs.alert.MyAlertDialog
import com.powerly.ui.dialogs.alert.rememberAlertDialogState
import kotlinx.coroutines.launch

private const val TAG = "PasswordEnterScreen"

/**
 * Composable function for the email and password sign in screen.
 *
 * @param viewModel The [EmailLoginViewModel] for managing email and password sign in.
 * @param navigateToHome Callback to navigate to the home screen.
 * @param onBack Callback to navigate back to the previous screen.
 */
@Composable
internal fun EmailPasswordEnterScreen(
    viewModel: EmailLoginViewModel,
    navigateToHome: () -> Unit,
    navigateToPasswordReset: () -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val forgetPasswordDialog = rememberAlertDialogState()
    val screenState = remember { viewModel.screenState }
    val email by remember { viewModel.email }
    var password by remember { viewModel.password }

    fun forgetPassword() {
        coroutineScope.launch {
            val reset = viewModel.forgetPassword(email)
            if (reset) navigateToPasswordReset()
        }
    }

    fun login(it: String) {
        password = it
        coroutineScope.launch {
            val loggedIn = viewModel.emailLogin(email, password)
            if (loggedIn) navigateToHome()
        }
    }


    MyAlertDialog(
        state = forgetPasswordDialog,
        title = stringResource(R.string.login_email_password_forget),
        message = stringResource(R.string.login_email_password_forget_msg, email),
        positiveButton = stringResource(R.string.yes),
        negativeButton = stringResource(R.string.no),
        positiveButtonClick = ::forgetPassword,
        properties = AlertDialogProperties(showClose = true)
    )

    PasswordEnterScreenContent(
        screenState = screenState,
        userPassword = password,
        userEmail = email,
        onForgetPassword = { forgetPasswordDialog.show() },
        onContinue = ::login,
        onBack = onBack
    )
}