package com.powerly

import android.content.Context
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
import com.powerly.resources.R
import com.powerly.ui.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val localeManager: LocaleManager,
    private val storageManager: StorageManager,
    private val paymentManager: PaymentManager,
    private val deviceHelper: DeviceHelper
) : ViewModel() {

    val uiState = HomeUiState(deviceHelper)
    val isLoggedIn: Boolean get() = storageManager.isLoggedIn

    fun initUiState(context: Context) {
        with(uiState) {
            languageName.value = localeManager.getLanguageName()
            languageCode.value = storageManager.currentLanguage
            isLoggedIn.value = storageManager.isLoggedIn
            if (isLoggedIn.value) {
                updateUserState(storageManager.userDetails!!)
            } else {
                userName.value = context.getString(R.string.profile_guest)
            }
        }
    }

    fun refreshUser() {
        val user = storageManager.userDetails
        if (user != null) updateUserState(user)
        else { uiState.isLoggedIn.value = false }
    }

    private fun updateUserState(details: User) {
        Log.v(TAG,"user-balance ${details.balance}")
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
        if (isLoggedIn) {
            paymentManager.initCardScanner(activity)
            paymentManager.initNextActionForPayment(activity)
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}