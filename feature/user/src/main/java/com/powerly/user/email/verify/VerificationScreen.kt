package com.powerly.user.email.verify

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.powerly.user.email.EmailLoginViewModel
import kotlinx.coroutines.launch

private const val TAG = "RVerificationScreen"

/**
 * Composable function for the email verification screen.
 *
 * @param viewModel The [EmailLoginViewModel] for handling registration operations.
 * @param navigateToHome Callback function to navigate to the home screen.
 * @param onBack Callback function to navigate back.
 */
@Composable
internal fun RegisterVerificationScreen(
    viewModel: EmailLoginViewModel,
    navigateToHome: () -> Unit,
    callSupport: () -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val screenState = remember { viewModel.screenState }
    var resetPin by remember { viewModel.resetPin }
    var resetCounter by remember { viewModel.resetCounter }

    val email by remember { viewModel.email }

    BackHandler(onBack = {})

    fun verifyCode(pin: String) {
        coroutineScope.launch {
            val verified = viewModel.emailVerify(pin)
            if (verified) screenState.showSuccess {
                navigateToHome()
            }
        }
    }

    VerificationScreenContent(
        screenState = screenState,
        resetPin = { resetPin },
        resetCounter = { resetCounter },
        userId = email,
        timeout = 60,
        uiEvents = {
            when (it) {
                is VerificationEvents.Help -> callSupport()
                is VerificationEvents.Edit -> onBack()
                is VerificationEvents.Next -> verifyCode(it.code)
                is VerificationEvents.ResendCode -> viewModel.resendPinCode()
            }
        }
    )

}


