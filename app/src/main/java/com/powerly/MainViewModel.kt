package com.powerly

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.user.User
import com.powerly.core.network.DeviceHelper
import com.powerly.lib.managers.LocaleManager
import com.powerly.lib.managers.StorageManager
import com.powerly.payment.PaymentManager
import com.powerly.ui.HomeUiState
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MainViewModel (
    private val userRepository: UserRepository,
    private val localeManager: LocaleManager,
    private val storageManager: StorageManager,
    private val paymentManager: PaymentManager,
    private val deviceHelper: DeviceHelper
) : ViewModel() {

    val uiState = HomeUiState(deviceHelper)
    val isLoggedIn: Boolean get() = storageManager.isLoggedIn

    fun initUiState() {
        with(uiState) {
            languageName.value = localeManager.getLanguageName()
            languageCode.value = storageManager.currentLanguage
            isLoggedIn.value = storageManager.isLoggedIn
            if (isLoggedIn.value) {
                updateUserState(storageManager.userDetails!!)
            } else {
                userName.value = ""
            }
        }
    }

    fun refreshUser() {
        val user = storageManager.userDetails
        if (user != null) updateUserState(user)
        else {
            uiState.isLoggedIn.value = false
            uiState.userName.value = ""
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


    fun getUserDetails() {
        viewModelScope.launch {
            val it = userRepository.getUserDetails()
            when (it) {
                is ApiStatus.Success -> {
                    val user = it.data
                    storageManager.userDetails = user
                    updateUserState(user)
                }

                else -> {}
            }
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