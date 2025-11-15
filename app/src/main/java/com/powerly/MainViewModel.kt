package com.powerly

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.user.User
import com.powerly.core.network.DeviceHelper
import com.powerly.lib.managers.PusherManager
import com.powerly.payment.PaymentManager
import com.powerly.ui.HomeUiState
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MainViewModel(
    private val userRepository: UserRepository,
    private val paymentManager: PaymentManager,
    private val pusherManager: PusherManager,
    private val deviceHelper: DeviceHelper
) : ViewModel() {

    val uiState = HomeUiState(deviceHelper)

    init {
        viewModelScope.launch {
            with(uiState) {
                languageCode.value = userRepository.getLanguage()
                isLoggedIn.value = userRepository.isLoggedIn()
            }
        }

        viewModelScope.launch {
            userRepository.userFlow.collect { user ->
                if (user != null) {
                    Log.v(TAG, "user-updated = $user")
                    updateUserState(user)
                } else {
                    uiState.isLoggedIn.value = false
                    uiState.userName.value = ""
                }
            }
        }

        viewModelScope.launch {
            userRepository.loggedInFlow.collect { loggedIn ->
                Log.v(TAG, "user-logged-in = $loggedIn")
                uiState.isLoggedIn.value = loggedIn
                if (loggedIn) {
                    pusherManager.initPusherManager()
                } else {
                    pusherManager.unsubscribeAll()
                    pusherManager.disconnect()
                }
            }
        }

        viewModelScope.launch {
            userRepository.languageFlow.collect {
                Log.v(TAG, "language-changed = $it")
                uiState.languageCode.value = it
            }
        }
    }

    private fun updateUserState(details: User) {
        with(uiState) {
            isLoggedIn.value = true
            userName.value = details.firstName
                .ifBlank { details.lastName }
                .ifBlank { details.email.substringBefore("@", "") }
            currency.value = details.currency
            balance.value = details.balance.toString()
        }
    }

    fun initPaymentManager(activity: ComponentActivity) {
        paymentManager.initCardScanner(activity)
        paymentManager.initNextActionForPayment(activity)
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}