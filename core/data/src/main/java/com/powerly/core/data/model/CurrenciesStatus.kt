package com.powerly.core.data.model

import com.powerly.core.model.location.AppCurrency
import com.powerly.core.model.util.Message

sealed class CurrenciesStatus {
    data class Error(val msg: Message) : CurrenciesStatus()
    data class Success(val data: List<AppCurrency>) : CurrenciesStatus()
    data object Loading : CurrenciesStatus()
}

