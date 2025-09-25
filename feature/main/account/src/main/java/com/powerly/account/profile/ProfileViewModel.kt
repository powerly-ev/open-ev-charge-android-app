package com.powerly.account.profile

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.data.model.CurrenciesStatus
import com.powerly.core.data.repositories.AppRepository
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.location.Country
import com.powerly.core.model.user.User
import com.powerly.core.model.user.UserUpdateBody
import com.powerly.core.network.DeviceHelper
import com.powerly.lib.managers.CountryManager
import com.powerly.lib.managers.NotificationsManager
import com.powerly.lib.managers.StorageManager
import com.powerly.ui.dialogs.loading.initScreenState
import org.koin.android.annotation.KoinViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@KoinViewModel
class ProfileViewModel (
    private val userRepository: UserRepository,
    private val appRepository: AppRepository,
    private val storageManager: StorageManager,
    private val deviceHelper: DeviceHelper,
    private val countryManager: CountryManager,
    private val notificationsManager: NotificationsManager,
) : ViewModel() {
    val user = mutableStateOf<User?>(null)
    val userCountry = mutableStateOf(Country(1))
    val screenState = initScreenState()
    private var profileUpdated: Boolean = false

    init {
        user.value = storageManager.userDetails
        countryManager.getSavedCountry()?.let {
            userCountry.value = it
        }
        Log.v(TAG, "user = $user")
        Log.v(TAG, "userCountry = ${userCountry.value.name}")
    }

    /**
     * Updates the user profile with the provided [newUser] data.
     *
     * Constructs a [UserUpdateBody] with the given user details and makes an API call to update the user profile.
     * If the update is successful, it refreshes the stored user data, triggers phone verification if necessary,
     * and displays a success dialog. It also handles pinning the user's currency if a new currency ID is provided.
     *
     * @param newUser The updated user details.
     * @param password The user's password (optional).
     * @param currency The new currency (optional).
     */
    fun updateProfile(
        newUser: User? = null,
        password: String? = null,
        currency: String? = null,
        countryId: Int? = null
    ) {
        Log.i(TAG, "updateProfile - $newUser")
        val request = UserUpdateBody(
            firstName = newUser?.firstName,
            lastName = newUser?.lastName,
            email = newUser?.email,
            vatId = newUser?.vatId,
            countryId = countryId,
            currency = currency,
            password = password
        )
        viewModelScope.launch {
            screenState.loading = true
            val result = userRepository.updateUserDetails(request)
            screenState.loading = false
            when (result) {
                is ApiStatus.Error -> screenState.showMessage(result.msg)
                is ApiStatus.Success -> {
                    user.value = result.data
                    profileUpdated = true
                    storageManager.userDetails = result.data
                    screenState.showSuccess()
                    if (currency != null) {
                        storageManager.pinUserCurrency = true
                    }
                }

                else -> {}
            }
        }
    }

    fun isProfileUpdated(): Boolean {
        val updated = profileUpdated
        profileUpdated = false
        return updated
    }

    fun updateUserCountry(country: Country) {
        updateProfile(countryId = country.id)
        userCountry.value = country
    }

    fun getUserCountry() = userCountry.value

    /**
     * Fetches a list of currencies and updates the UI state accordingly.
     * the retrieved currencies are sorted alphabetically
     * by their ISO codes and added to the `currencies` list.
     */
    val currencies: Flow<CurrenciesStatus> = flow {
        val result = appRepository.getCurrencies()
        emit(result)
    }.stateIn(
        scope = viewModelScope,
        initialValue = CurrenciesStatus.Loading,
        started = SharingStarted.Lazily
    )

    /**
     * Logs the user out.
     * Disables notifications, performs logout API call, clears user data.
     */
    suspend fun logout(): Boolean {
        Log.i(TAG, "logout")
        if (storageManager.isLoggedIn) {
            val imei = storageManager.imei()
            screenState.loading = true
            val it = userRepository.logout(imei)
            screenState.loading = false
            when (it) {
                is ApiStatus.Error -> screenState.showMessage(it.msg)
                is ApiStatus.Success -> {
                    logoutDevice()
                    return true
                }

                else -> {}
            }
        }
        return false
    }

    /**
     * Deletes the user account.
     * Performs the account deletion API call, clears user data.
     */
    suspend fun deleteAccount(): Boolean {
        Log.i(TAG, "deleteAccount")
        // delete account
        screenState.loading = true
        val it = userRepository.deleteUser()
        screenState.loading = false
        when (it) {
            is ApiStatus.Error -> screenState.showMessage(it.msg)
            is ApiStatus.Success -> {
                logoutDevice()
                return true
            }

            else -> {}
        }
        return false
    }

    private suspend fun logoutDevice() {
        storageManager.logOutAll()
        storageManager.showRegisterNotification = true
        notificationsManager.clearNotifications()
    }

    val appLink: String get() = deviceHelper.appLink


    companion object {
        private const val TAG = "ProfileViewModel"
    }
}