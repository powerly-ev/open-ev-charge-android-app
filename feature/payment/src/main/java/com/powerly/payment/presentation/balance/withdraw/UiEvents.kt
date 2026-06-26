package com.powerly.payment.presentation.balance.withdraw

sealed class WithdrawEvents {
    data object Close : WithdrawEvents()
    data object PrivacyPolicy : WithdrawEvents()
    data object TermsOfService : WithdrawEvents()
    data object Withdraw : WithdrawEvents()
}
