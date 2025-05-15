package com.powerly.user.email.password.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalAutofillManager
import com.powerly.user.UserViewModel
import com.powerly.user.email.EmailLoginViewModel
import kotlinx.coroutines.launch

private const val TAG = "PasswordCreateScreen"

/**
 * Composable function for the email and password sign up screen.
 *
 * @param userViewModel The [UserViewModel] for managing user data.
 * @param viewModel The [EmailLoginViewModel] for managing email and password sign up.
 * @param onSelectCountry Callback to show countries dialog to select user country.
 * @param navigateToVerification Callback to navigate to the email verification screen.
 * @param onBack Callback to navigate back to the previous screen.
 */
@Composable
internal fun EmailPasswordCreateScreen(
    userViewModel: UserViewModel,
    viewModel: EmailLoginViewModel,
    onSelectCountry: () -> Unit,
    navigateToVerification: () -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val autofillManager = LocalAutofillManager.current
    val screenState = remember { viewModel.screenState }
    var country by remember { userViewModel.country }
    val email by remember { viewModel.email }
    var password by remember { viewModel.password }


    PasswordCreateScreenContent(
        screenState = screenState,
        userPassword = password,
        userEmail = email,
        userCountry = { country?.name },
        onSelectCountry = onSelectCountry,
        onContinue = {
            password = it
            coroutineScope.launch {
                if (country == null) return@launch
                val registered = viewModel.register(country!!)
                if (registered) {
                    autofillManager?.commit()
                    navigateToVerification()
                }
            }
        },
        onBack = onBack
    )
}