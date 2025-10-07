package com.powerly.user

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.data.repositories.AppRepository
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.location.Country
import com.powerly.core.network.DeviceHelper
import com.powerly.lib.managers.CountryManager
import com.powerly.user.reminder.ReminderManager
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class UserViewModel(
    private val userRepository: UserRepository,
    private val appRepository: AppRepository,
    private val countryManager: CountryManager,
    private val reminderManager: ReminderManager,
    private val deviceHelper: DeviceHelper,
) : ViewModel() {

    val country = mutableStateOf<Country?>(null)
    val isLoggedIn: Boolean get() = userRepository.isLoggedIn

    fun setCountry(country: Country) {
        this.country.value = country
    }

    fun updateDevice() {
        viewModelScope.launch {
            appRepository.updateDevice()
        }
    }

    fun detectCountry() {
        viewModelScope.launch {
            country.value = countryManager.detectActualCountry()
            Log.v("UserViewModel", "country - ${country.value}")
        }
    }


    fun cancelRegistrationReminders() {
        if (userRepository.isLoggedIn)
            reminderManager.cancelRegistrationReminders()
    }

    fun initRegistrationReminders() {
        if (appRepository.showRegisterNotification()) {
            reminderManager.initRegistrationReminders()
        }
    }


    val appVersion: String
        get() = String.format(
            "Version : %s %s",
            deviceHelper.buildType,
            deviceHelper.appVersion
        )

    val supportNumber: String get() = deviceHelper.supportNumber

}