package com.powerly.user.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.domain.repository.AppRepository
import com.powerly.core.domain.model.location.Country
import com.powerly.core.domain.model.AppInfo
import com.powerly.user.domain.use_case.CancelRegistrationRemindersUseCase
import com.powerly.user.domain.use_case.DetectCountryUseCase
import com.powerly.user.domain.use_case.InitRegistrationRemindersUseCase
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel


@KoinViewModel
class UserViewModel(
    private val appRepository: AppRepository,
    private val detectCountryUseCase: DetectCountryUseCase,
    private val cancelRegistrationRemindersUseCase: CancelRegistrationRemindersUseCase,
    private val initRegistrationRemindersUseCase: InitRegistrationRemindersUseCase,
    private val appInfo: AppInfo,
) : ViewModel() {

    val country = mutableStateOf<Country?>(null)
    val countries = mutableStateListOf<Country>()

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
            countries.clear()
            val result = detectCountryUseCase()
            countries.addAll(result.countries)
            Log.v(TAG, "getCountries - ${countries.size}")
            country.value = result.country
            Log.v(TAG, "country - ${country.value}")
        }
    }


    fun cancelRegistrationReminders() {
        viewModelScope.launch {
            cancelRegistrationRemindersUseCase()
        }
    }

    fun initRegistrationReminders() {
        viewModelScope.launch {
            initRegistrationRemindersUseCase()
        }
    }


    val appVersion: String
        get() = String.format(
            "Version : %s %s",
            appInfo.buildType,
            appInfo.appVersion
        )

    val supportNumber: String get() = appInfo.supportNumber

    companion object {
        private const val TAG = "UserViewModel"
    }
}
