package com.powerly.account.profile

import com.powerly.core.model.location.AppCurrency
import com.powerly.core.model.user.User

internal sealed class ProfileEvents {
    data object Close : ProfileEvents()
    data object SignOut : ProfileEvents()
    data object SelectCountry : ProfileEvents()
    data class SelectCurrency(val currency: AppCurrency) : ProfileEvents()
    data object DeleteAccount : ProfileEvents()
    data class Save(
        val newUser: User,
        val password: String?
    ) : ProfileEvents()
}
