package com.powerly.user.email.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.powerly.user.email.EmailLoginViewModel
import kotlinx.coroutines.launch

private const val TAG = "EmailLoginScreen"

@Composable
internal fun EmailLoginScreen(
    viewModel: EmailLoginViewModel,
    navigateToPassword: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val screenState = remember { viewModel.screenState }
    var email by remember { viewModel.email }

    EmailLoginScreenContent(
        screenState = screenState,
        userEmail = email,
        onContinue = {
            email = it
            viewModel.password.value = ""
            coroutineScope.launch {
                val isNewUser = viewModel.checkEmail(email)
                navigateToPassword(isNewUser)
            }
        },
        onBack = onBack
    )
}