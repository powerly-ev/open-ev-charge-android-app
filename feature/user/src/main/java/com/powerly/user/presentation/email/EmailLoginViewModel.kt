package com.powerly.user.presentation.email

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.location.Country
import com.powerly.ui.dialogs.loading.initScreenState
import com.powerly.user.domain.model.LoginResult
import com.powerly.user.domain.repository.LoginEmailRepository
import com.powerly.user.domain.use_case.EmailLoginUseCase
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel


@KoinViewModel
class EmailLoginViewModel(
    private val loginRepository: LoginEmailRepository,
    private val emailLoginUseCase: EmailLoginUseCase,
) : ViewModel() {

    val screenState = initScreenState()
    val resetPin = mutableStateOf(false)
    val resetCounter = mutableStateOf(false)
    val counterTimeout = mutableIntStateOf(60)
    val email = mutableStateOf("")
    val password = mutableStateOf("")

    fun resetInputs() {
        email.value = ""
        password.value = ""
    }

    suspend fun checkEmail(email: String): Boolean {
        screenState.loading = true
        val result = loginRepository.emailCheck(email)
        screenState.loading = false
        when (result) {
            is ApiStatus.Success -> {
                Log.i(TAG, "emailCheck - ${result.data}")
                return result.data.newUser
            }

            is ApiStatus.Error -> screenState.showMessage(result.msg)
            else -> {}
        }
        return false
    }

    /**
     * Logs in the user with the provided email and password. Delegates to
     * [EmailLoginUseCase] for the verify-resend fallback on UNAUTHENTICATED.
     */
    suspend fun emailLogin(email: String, password: String): LoginResult {
        screenState.loading = true
        val result = emailLoginUseCase(email, password)
        screenState.loading = false
        Log.i(TAG, "login outcome: ${result.outcome}")
        result.error?.let { screenState.showMessage(it) }
        if (result.outcome == LoginResult.VERIFICATION_REQUIRED) {
            this.email.value = email
        }
        return result.outcome
    }

    suspend fun register(country: Country): Boolean {
        screenState.loading = true
        val result = loginRepository.emailRegister(
            email = email.value,
            password = password.value,
            countryId = country.id
        )
        screenState.loading = false
        when (result) {
            is ApiStatus.Success -> return true
            is ApiStatus.Error -> screenState.showMessage(result.msg)
            else -> {}
        }
        return false
    }


    suspend fun emailVerify(pin: String): Boolean {
        screenState.loading = true
        val result = loginRepository.emailVerify(code = pin, email = email.value)
        screenState.loading = false
        when (result) {
            is ApiStatus.Success -> return true
            is ApiStatus.Error -> {
                resetPin.value = resetPin.value.not()
                screenState.showMessage(result.msg)
            }

            else -> {}
        }
        return false
    }


    fun resendPinCode() {
        viewModelScope.launch {
            Log.i(TAG, "resendPinCode")
            // Toggle the reset pin flag.
            // this clears the existing pin code field
            resetPin.value = resetPin.value.not()
            screenState.loading = true
            val result = loginRepository.emailVerifyResend(email.value)
            screenState.loading = false
            when (result) {
                is ApiStatus.Success -> {
                    // Toggle the reset counter flag.
                    // this restart new verification timeout timer
                    resetCounter.value = resetCounter.value.not()
                }

                is ApiStatus.Error -> screenState.showMessage(result.msg)
                else -> {}
            }
        }
    }

    suspend fun forgetPassword(email: String): Int? {
        screenState.loading = true
        val result = loginRepository.emailForgetPassword(email)
        screenState.loading = false
        when (result) {
            is ApiStatus.Success -> return result.data.canResendInSeconds
            is ApiStatus.Error -> screenState.showMessage(result.msg)
            else -> {}
        }
        return null
    }

    fun forgetPasswordResendCode() {
        viewModelScope.launch {
            screenState.loading = true
            val result = loginRepository.emailForgetPassword(email.value)
            screenState.loading = false
            when (result) {
                is ApiStatus.Success -> {
                    resetPin.value = resetPin.value.not()
                    counterTimeout.intValue = result.data.canResendInSeconds
                    resetCounter.value = resetCounter.value.not()
                }

                is ApiStatus.Error -> screenState.showMessage(result.msg)
                else -> {}
            }
        }
    }

    suspend fun resetPassword(pin: String, password: String): Boolean {
        screenState.loading = true
        val result = loginRepository.emailResetPassword(
            pin = pin,
            email = email.value,
            password = password
        )
        screenState.loading = false
        when (result) {
            is ApiStatus.Success -> return true
            is ApiStatus.Error -> {
                resetPin.value = resetPin.value.not()
                screenState.showMessage(result.msg)
            }

            else -> {}
        }
        return false
    }

    companion object {
        private const val TAG = "EmailLoginViewModel"
    }
}
