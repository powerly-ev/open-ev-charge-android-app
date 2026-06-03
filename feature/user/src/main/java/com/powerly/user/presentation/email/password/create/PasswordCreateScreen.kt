package com.powerly.user.presentation.email.password.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalAutofillManager
import com.powerly.user.presentation.UserViewModel
import com.powerly.user.presentation.email.EmailLoginViewModel
import kotlinx.coroutines.launch

private const val TAG = "PasswordCreateScreen"

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