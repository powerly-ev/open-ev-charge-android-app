package com.powerly.user.email

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.data.repositories.LoginEmailRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.location.Country
import com.powerly.core.model.user.EmailCheck
import com.powerly.core.model.user.EmailLoginBody
import com.powerly.core.model.user.EmailRegisterBody
import com.powerly.core.model.user.EmailResetBody
import com.powerly.core.model.user.User
import com.powerly.core.model.user.VerificationBody
import com.powerly.lib.managers.StorageManager
import com.powerly.ui.dialogs.loading.initScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailLoginViewModel @Inject constructor(
    private val loginRepository: LoginEmailRepository,
    private val storageManager: StorageManager
) : ViewModel() {

    val screenState = initScreenState()

    val resetPin = mutableStateOf(false)
    val resetCounter = mutableStateOf(false)
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
                val emailCheck: EmailCheck = result.data
                Log.i(TAG, "emailCheck - $emailCheck")
                return emailCheck.newUser
            }

            is ApiStatus.Error -> screenState.showMessage(result.msg)
            else -> {}
        }
        return false
    }

    /**
     * Logs in the user with the provided email and password.
     */
    suspend fun emailLogin(email: String, password: String): Boolean {
        // Get the device token
        screenState.loading = true
        val imei = storageManager.imei()
        val body = EmailLoginBody(email, password, imei)
        val result = loginRepository.emailLogin(body)
        screenState.loading = false
        when (result) {
            is ApiStatus.Success -> {
                Log.i(TAG, "user - ${result.data}")
                saveLogin(result.data)
                return true
            }

            is ApiStatus.Error -> {
                screenState.showMessage(result.msg)
            }

            else -> {}
        }
        return false
    }


    /**
     * Registers the user with the provided email and password.
     */
    suspend fun register(country: Country): Boolean {
        // Create the EmailRegisterRequest object
        val request = EmailRegisterBody(
            email = email.value,
            password = password.value,
            countryId = country.id,
            deviceImei = storageManager.imei()
        )
        screenState.loading = true
        val result = loginRepository.emailRegister(request)
        screenState.loading = false
        when (result) {
            is ApiStatus.Success -> return true
            is ApiStatus.Error -> {
                screenState.showMessage(result.msg)
            }

            else -> {}
        }
        return false
    }


    suspend fun emailVerify(pin: String): Boolean {
        screenState.loading = true
        val request = VerificationBody(code = pin, email = email.value)
        val result = loginRepository.emailVerify(request)
        screenState.loading = false
        // Handle the different API states.
        when (result) {
            is ApiStatus.Success -> {
                saveLogin(result.data)
                return true
            }

            is ApiStatus.Error -> {
                resetPin.value = resetPin.value.not()
                screenState.showMessage(result.msg)
            }

            else -> {}
        }
        return false
    }


    /**
     * Once expired, resends the PIN code to the user's phone number again.
     */
    fun resendPinCode() {
        viewModelScope.launch {
            Log.i(TAG, "resendPinCode")
            // Launch a coroutine to collect the phone resend code flow.
            // Toggle the reset pin flag.
            // this clears the existing pin code field
            resetPin.value = resetPin.value.not()
            screenState.loading = false
            val result = loginRepository.emailVerifyResend(email.value)
            screenState.loading = true
            // Handle the different API states.
            when (result) {
                is ApiStatus.Success -> {
                    // Toggle the reset counter flag.
                    // this restart new verification timeout timer
                    resetCounter.value = resetCounter.value.not()
                }

                is ApiStatus.Error -> {
                    screenState.showMessage(result.msg)
                }

                else -> {}
            }
        }
    }

    /**
     * Reset Password
     */

    suspend fun forgetPassword(email: String): Boolean {
        screenState.loading = true
        val result = loginRepository.emailForgetPassword(email)
        screenState.loading = false
        when (result) {
            is ApiStatus.Success -> return true
            is ApiStatus.Error -> {
                screenState.showMessage(result.msg)
            }

            else -> {}
        }
        return false
    }

    suspend fun resetPassword(pin: String, password: String): Boolean {
        screenState.loading = true
        val body = EmailResetBody(
            code = pin,
            email = email.value,
            password = password
        )
        val result = loginRepository.emailResetPassword(body)
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

    private fun saveLogin(user: User) {
        val token = user.accessToken ?: return
        storageManager.userToken = token
        storageManager.userDetails = user
        storageManager.currency = user.currency
    }

    companion object {
        private const val TAG = "EmailLoginViewModel"
    }
}