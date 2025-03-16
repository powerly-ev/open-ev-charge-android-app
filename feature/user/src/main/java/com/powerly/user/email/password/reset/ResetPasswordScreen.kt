package com.powerly.user.email.password.reset

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.powerly.user.email.EmailLoginViewModel
import kotlinx.coroutines.launch


private const val TAG = "PasswordResetScreen"


@Composable
internal fun EmailPasswordResetScreen(
    viewModel: EmailLoginViewModel,
    onBack: () -> Unit
) {

    val coroutineScope = rememberCoroutineScope()
    val screenState = remember { viewModel.screenState }
    var resetPin by remember { mutableStateOf(false) }
    val email by remember { viewModel.email }

    BackHandler(onBack = {})

    fun resetPassword(pin: String, password: String) {
        coroutineScope.launch {
            val reset = viewModel.resetPassword(pin, password)
            if (reset) screenState.showSuccess {
                viewModel.password.value = password
                onBack()
            }
        }
    }

    ResetPasswordScreenContent(
        screenState = screenState,
        resetPin = { resetPin },
        userId = email,
        uiEvents = {
            when (it) {
                is EmailResetEvents.Edit -> onBack()
                is EmailResetEvents.Next -> resetPassword(it.pin, it.password)
            }
        }
    )
}


