package com.powerly.user.welcome.agreement

import androidx.lifecycle.ViewModel
import com.powerly.core.data.repositories.AppRepository
import com.powerly.lib.managers.CountryManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserAgreementViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val countryManager: CountryManager
) : ViewModel() {

    /**
     * Fetches the user agreements based on country ID.
     *
     * @param type The type of user agreement to fetch.
     */
    suspend fun getUserAgreementLink(type: Int): String? {
        return null
    }
}