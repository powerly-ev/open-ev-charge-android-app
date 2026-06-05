package com.powerly.core.domain.model

import com.powerly.core.model.location.AppCurrency
import com.powerly.core.domain.model.Message

sealed class CurrenciesStatus {
    data class Error(val msg: Message) : CurrenciesStatus()
    data class Success(val data: List<AppCurrency>) : CurrenciesStatus()
    data object Loading : CurrenciesStatus()
}
