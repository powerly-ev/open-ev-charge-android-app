package com.powerly.account.presentation.profile

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.domain.model.CurrenciesStatus
import com.powerly.core.domain.repository.AppRepository
import com.powerly.core.domain.repository.UserRepository
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.location.Country
import com.powerly.core.domain.model.user.User
import com.powerly.core.domain.model.AppInfo
import com.powerly.core.managers.NotificationsManager
import com.powerly.ui.dialogs.loading.initScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class ProfileViewModel(
    private val userRepository: UserRepository,
    private val appRepository: AppRepository,
    private val appInfo: AppInfo,
    private val notificationsManager: NotificationsManager,
) : ViewModel() {
    val userCountry = mutableStateOf(Country(1))
    var countries = listOf<Country>()
        private set
    val screenState = initScreenState()

    init {
        viewModelScope.launch {
            countries = appRepository.getCountriesList()
            appRepository.getUserCountry()?.let { userCountry.value = it }
            Log.v(TAG, "userCountry = ${userCountry.value.name}")
        }
    }

    val userFlow = userRepository.userFlow.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.Lazily
    )

    fun updateProfile(
        newUser: User? = null,
        password: String? = null,
        currency: String? = null,
        countryId: Int? = null
    ) {
        Log.i(TAG, "updateProfile - $newUser")
        viewModelScope.launch {
            screenState.loading = true
            val result = userRepository.updateUserDetails(
                firstName = newUser?.firstName,
                lastName = newUser?.lastName,
                email = newUser?.email,
                vatId = newUser?.vatId,
                countryId = countryId,
                currency = currency,
                password = password
            )
            screenState.loading = false
            when (result) {
                is ApiStatus.Error -> {
                    screenState.showMessage(result.msg)
                }

                is ApiStatus.Success -> {
                    screenState.showSuccess()
                }

                else -> {}
            }
        }
    }

    fun updateUserCountry(country: Country) {
        updateProfile(countryId = country.id)
        userCountry.value = country
    }

    fun getUserCountry() = userCountry.value

    val currencies: Flow<CurrenciesStatus> = flow {
        val result = appRepository.getCurrencies()
        emit(result)
    }.stateIn(
        scope = viewModelScope,
        initialValue = CurrenciesStatus.Loading,
        started = SharingStarted.Lazily
    )

    suspend fun logout(): Boolean {
        Log.i(TAG, "logout")
        if (userRepository.isLoggedIn()) {
            screenState.loading = true
            val it = userRepository.logout()
            screenState.loading = false
            when (it) {
                is ApiStatus.Error -> screenState.showMessage(it.msg)
                is ApiStatus.Success -> {
                    notificationsManager.clearNotifications()
                    return true
                }

                else -> {}
            }
        }
        return false
    }

    suspend fun deleteAccount(): Boolean {
        Log.i(TAG, "deleteAccount")
        screenState.loading = true
        val it = userRepository.deleteUser()
        screenState.loading = false
        when (it) {
            is ApiStatus.Error -> screenState.showMessage(it.msg)
            is ApiStatus.Success -> {
                notificationsManager.clearNotifications()
                return true
            }

            else -> {}
        }
        return false
    }


    val appLink: String get() = appInfo.appLink


    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
