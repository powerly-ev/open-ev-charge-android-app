package com.powerly.user.email.password.enter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.res.stringResource
import com.powerly.user.email.EmailLoginViewModel
import com.powerly.resources.R
import com.powerly.ui.dialogs.alert.AlertDialogProperties
import com.powerly.ui.dialogs.alert.MyAlertDialog
import com.powerly.ui.dialogs.alert.rememberAlertDialogState
import com.powerly.user.email.LoginResult
import kotlinx.coroutines.launch

private const val TAG = "PasswordEnterScreen"

/**
 * Composable function for the email and password sign in screen.
 *
 */
@Composable
internal fun EmailPasswordEnterScreen(
    viewModel: EmailLoginViewModel,
    navigateToHome: () -> Unit,
    navigateToVerification: () -> Unit,
    navigateToPasswordReset: (Int) -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val autoFillManager = LocalAutofillManager.current
    val forgetPasswordDialog = rememberAlertDialogState()
    val screenState = remember { viewModel.screenState }
    val email by remember { viewModel.email }
    var password by remember { viewModel.password }

    fun forgetPassword() {
        coroutineScope.launch {
            val timeout = viewModel.forgetPassword(email)
            if (timeout != null) navigateToPasswordReset(timeout)
        }
    }

    fun login(it: String) {
        password = it
        coroutineScope.launch {
            val state = viewModel.emailLogin(email, password)
            when (state) {
                LoginResult.SUCCESS -> {
                    autoFillManager?.commit()
                    navigateToHome()
                }

                LoginResult.VERIFICATION_REQUIRED -> navigateToVerification()
                LoginResult.ERROR -> {}
            }
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