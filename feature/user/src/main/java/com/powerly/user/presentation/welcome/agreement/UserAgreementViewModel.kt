package com.powerly.user.presentation.welcome.agreement

import androidx.lifecycle.ViewModel
import com.powerly.core.domain.model.AppInfo
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class UserAgreementViewModel(
    private val appInfo: AppInfo
) : ViewModel() {

    /**
     * Returns the URL to render in the user-agreement web view.
     *
     * @param type 1 = privacy policy, anything else = terms & conditions.
     */
    fun getUserAgreementLink(type: Int): String? {
        return if (type == 1) appInfo.privacyPolicyUrl
        else appInfo.termsAndConditionsUrl
    }
}
