package com.powerly.account

sealed class AccountEvents {
    data object OpenProfile : AccountEvents()
    data object OpenBalance : AccountEvents()
    data object OpenWallet : AccountEvents()
    data object OpenVehicles : AccountEvents()
    data object OpenSupport : AccountEvents()
    data object OpenInvite : AccountEvents()
    data object LanguageDialog : AccountEvents()
}