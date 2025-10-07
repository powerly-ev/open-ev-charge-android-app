package com.powerly

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.user.User
import com.powerly.core.network.DeviceHelper
import com.powerly.lib.managers.LocaleManager
import com.powerly.payment.PaymentManager
import com.powerly.ui.HomeUiState
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MainViewModel(
    private val userRepository: UserRepository,
    private val localeManager: LocaleManager,
    private val paymentManager: PaymentManager,
    private val deviceHelper: DeviceHelper
) : ViewModel() {

    val uiState = HomeUiState(deviceHelper)

    init {
        viewModelScope.launch {
            with(uiState) {
                languageName.value = localeManager.getLanguageName()
                languageCode.value = localeManager.currentLanguage
                isLoggedIn.value = userRepository.isLoggedIn
            }
            userRepository.userFlow.collect { user ->
                if (user != null) {
                    updateUserState(user)
                } else {
                    uiState.isLoggedIn.value = false
                    uiState.userName.value = ""
                }
            }
        }
    }

    private fun updateUserState(details: User) {
        Log.v(TAG, "user-balance ${details.balance}")
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
        private const val TAG = "HomeViewModel"
    }
}