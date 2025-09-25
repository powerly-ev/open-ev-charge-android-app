package com.powerly.user.welcome.agreement

import androidx.lifecycle.ViewModel
import com.powerly.core.network.DeviceHelper
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class UserAgreementViewModel (
    private val deviceHelper: DeviceHelper
) : ViewModel() {

    /**
     * Fetches the user agreements based on country ID.
     *
     * @param type The type of user agreement to fetch.
     */
    fun getUserAgreementLink(type: Int): String? {
        return if (type == 1) deviceHelper.privacyPolicyUrl
        else deviceHelper.termsAndConditionsUrl
    }
}